package cn.netdiscovery.core.registry;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.config.SpiderEngineConfig;
import com.safframework.tony.common.utils.Preconditions;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 将当前 SpiderEngine 注册到 zookeeper 指定的目录 /netdiscovery 下
 * Created by tony on 2019-06-09.
 */
public class ZKRegistry extends Registry {

    public ZKRegistry() {

        this(SpiderEngineConfig.getInstance().getZkStr(), SpiderEngineConfig.getInstance().getZkPath());
    }

    public ZKRegistry(String zkStr,String zkPath) {

        provider = new Provider();
        provider.setConnectString(zkStr);
        provider.setPath(zkPath);
    }

    @Override
    public void register(Provider provider, int port) {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(provider.getConnectString(), retryPolicy);
        client.start();

        try {
            if (Preconditions.isBlank(provider.getPath())) {
                provider.setPath(Constant.DEFAULT_REGISTRY_PATH);
            }

            Stat stat = client.checkExists().forPath(provider.getPath());

            if (stat==null) { // 如果父目录不存在，则事先创建父目录
                client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(provider.getPath());
            }

            String ipAddr = InetAddress.getLocalHost().getHostAddress() + "-" + port + "-" + System.currentTimeMillis();
            String nowSpiderEngineZNode = provider.getPath() + "/" + ipAddr;
            client.create().withMode(CreateMode.EPHEMERAL).forPath(nowSpiderEngineZNode,nowSpiderEngineZNode.getBytes());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
