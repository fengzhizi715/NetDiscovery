package cn.netdiscovery.core.watch.zookeeper;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.config.SpiderEngineConfig;
import cn.netdiscovery.core.domain.SpiderEngineState;
import cn.netdiscovery.core.watch.AbstractWatchManager;
import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by tony on 2019-05-21.
 */
@Slf4j
public class ZooKeeperWatchManager extends AbstractWatchManager implements Watcher {

    private CuratorFramework client;

    private List<String> znodes; // 用于存储指定 zNode 下所有子 zNode 的名字

    public ZooKeeperWatchManager() {

        this(SpiderEngineConfig.getInstance().getZkStr(), SpiderEngineConfig.getInstance().getZkPath());
    }

    public ZooKeeperWatchManager(String zkStr, String zkPath) {

        if (Preconditions.isNotBlank(zkStr)) {
            log.info("zkStr: {}", zkStr);

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(zkStr, retryPolicy);
            // 在start方法之后书写具体的操作
            client.start();

            try {
                if (Preconditions.isBlank(zkPath)) {
                    this.path = Constant.DEFAULT_REGISTRY_PATH;
                } else {
                    this.path = zkPath;
                }

                Stat stat = client.checkExists().forPath(zkPath);

                if (stat==null) {
                    client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath);
                }

                znodes = client.getChildren().usingWatcher(this).forPath(zkPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Preconditions.isNotBlank(znodes)) {

                znodes.forEach(node->{

                    stateMap.put(node, SpiderEngineState.ONLINE);
                });
            }

            vertx = Vertx.vertx();
        }
    }

    /**
     * 当前所监控的父的 zNode 下若是子 zNode 发生了变化：新增，删除，修改
     * <p>
     * 下述方法都会触发执行
     *
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {

        List<String> newZodeInfos = null;
        try {
            newZodeInfos = client.getChildren().usingWatcher(this).forPath(path);
            //根据初始化容器的长度与最新的容器的长度进行比对，就可以推导出当前 SpiderEngine 集群的状态：新增，宕机/下线，变更...
            //哪个容器中元素多，就循环遍历哪个容器。
            if (Preconditions.isNotBlank(newZodeInfos)) {
                if (newZodeInfos.size()>znodes.size()){
                    //明确显示新增了哪个 SpiderEngine 节点
                    for (String nowZNode:newZodeInfos) {
                        if (!znodes.contains(nowZNode)){
                            log.info("新增 SpiderEngine 节点{}", nowZNode);
                            stateMap.put(nowZNode, SpiderEngineState.ONLINE);
                        }
                    }
                }else if (newZodeInfos.size()<znodes.size()){
                    // 宕机/下线
                    // 明确显示哪个 SpiderEngine 节点宕机/下线了
                    for (String initZNode : znodes) {
                        if (!newZodeInfos.contains(initZNode)) {
                            log.info("SpiderEngine 节点【{}】下线了！", initZNode);
                            stateMap.put(initZNode, SpiderEngineState.OFFLINE);

                            // 如果有下线的处理，则处理(例如发邮件、短信、重启等)
                            if (serverOfflineProcess!=null) {
                                serverOfflineProcess.process();
                            }
                        }
                    }
                }else {
                    // SpiderEngine 集群正常运行;
                    // 宕机/下线了，当时马上重启了，总数未发生变化
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        znodes = newZodeInfos;
    }

    @Override
    public void start(){
        while (true){

        }
    }

}