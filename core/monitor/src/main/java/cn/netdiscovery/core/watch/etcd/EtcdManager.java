package cn.netdiscovery.core.watch.etcd;

import cn.netdiscovery.core.config.Configuration;
import cn.netdiscovery.core.config.Constant;
import com.safframework.tony.common.utils.Preconditions;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;


/**
 * Created by tony on 2019-06-09.
 */
@Slf4j
public class EtcdManager {

    private Client client;

    public EtcdManager() {

        this(Configuration.getConfig("spiderEngine.registry.etcd.etcdStr"));
    }

    public EtcdManager(String etcdStr) {

        if (Preconditions.isNotBlank(etcdStr)) {
            client = Client.builder().endpoints(etcdStr).build();
        }
    }

    public void process() {

        CountDownLatch latch = new CountDownLatch(Integer.MAX_VALUE);
        Watch.Watcher watcher = null;

        String prefixPath = Configuration.getConfig("spiderEngine.registry.etcd.etcdPath");
        if (Preconditions.isBlank(prefixPath)) {
            prefixPath = Constant.DEFAULT_REGISTRY_PATH;
        }

        try {
            ByteSequence watchKey = ByteSequence.from("/"+prefixPath, StandardCharsets.UTF_8);
            WatchOption watchOpts = WatchOption.newBuilder().withRevision(0).withPrefix(ByteSequence.from(("/"+prefixPath).getBytes())).build();

            watcher = client.getWatchClient().watch(watchKey, watchOpts, response -> {
                        for (WatchEvent event : response.getEvents()) {
                            log.info("type={}, key={}, value={}",
                                    event.getEventType().toString(),
                                    Optional.ofNullable(event.getKeyValue().getKey())
                                            .map(bs -> bs.toString(StandardCharsets.UTF_8))
                                            .orElse(""),
                                    Optional.ofNullable(event.getKeyValue().getValue())
                                            .map(bs -> bs.toString(StandardCharsets.UTF_8))
                                            .orElse(""));
                        }

                        latch.countDown();
                    }
            );

            latch.await();
        } catch (Exception e) {
            if (watcher != null) {
                watcher.close();
            }
        }
    }

}
