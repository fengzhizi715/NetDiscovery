package cn.netdiscovery.core.registry;

import cn.netdiscovery.core.config.SpiderEngineConfig;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by tony on 2019-06-09.
 */
@Slf4j
public class EtcdRegistry extends Registry {

    private Lease lease;
    private KV kv;
    private long leaseId;

    public EtcdRegistry() {

        this(SpiderEngineConfig.getInstance().getEtcdStr(), SpiderEngineConfig.getInstance().getEtcdPath());
    }

    public EtcdRegistry(String etcdStr, String etcdPath) {

        provider = new Provider();
        provider.setConnectString(etcdStr);
        provider.setPath(etcdPath);
    }

    @Override
    public void register(Provider provider, int port) {

        Client client = Client.builder().endpoints(provider.getConnectString()).build();
        this.lease = client.getLeaseClient();
        this.kv = client.getKVClient();
        try {
            this.leaseId = lease.grant(5).get().getID();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        keepAlive();

        try {
            String ipAddr = InetAddress.getLocalHost().getHostAddress() + "-" + port + "-" + System.currentTimeMillis();
            String strKey = MessageFormat.format("/{0}/{1}", provider.getPath(), ipAddr);
            ByteSequence key = ByteSequence.from(strKey, StandardCharsets.UTF_8);
            String weight = "50";
            ByteSequence val = ByteSequence.from(weight, StandardCharsets.UTF_8);
            kv.put(key, val, PutOption.newBuilder().withLeaseId(leaseId).withPrevKV().build()).get();
            kv.txn();
            log.info("Register a new service at:{},weight:{}", strKey, weight);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送心跳到 etcd, 表明该host是活着的
     */
    private void keepAlive() {
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        lease.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                            @Override
                            public void onNext(LeaseKeepAliveResponse value) {

                            }

                            @Override
                            public void onError(Throwable t) {

                            }

                            @Override
                            public void onCompleted() {

                            }
                        });
                        log.info("KeepAlive lease:" + leaseId + "; Hex format:" + Long.toHexString(leaseId));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
        );
    }
}
