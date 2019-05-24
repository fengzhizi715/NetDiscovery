package cn.netdiscovery.core.curator;

import cn.netdiscovery.core.config.Configuration;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * Created by tony on 2019-05-21.
 */
@Slf4j
public class CuratorManager implements Watcher {

    private CuratorFramework client;

    /**
     * 容器，用于存储指定 zNode 下所有子 zNode 的名字
     */
    private List<String> initAllZnodes;

    private DownTimeProcess downTimeProcess;

    public CuratorManager() {

        String zkStr = Configuration.getConfig("spiderEngine.config.zkStr");

        if (Preconditions.isNotBlank(zkStr)) {
            log.info("zkStr: {}", zkStr);

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(zkStr, retryPolicy);
            //注意：在start方法之后书写具体的操作
            client.start();

            try {
                initAllZnodes = client.getChildren().usingWatcher(this).forPath("/netdiscovery");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CuratorManager downTimeProcess(DownTimeProcess downTimeProcess) {
        this.downTimeProcess = downTimeProcess;
        return this;
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
            newZodeInfos = client.getChildren().usingWatcher(this).forPath("/netdiscovery");
            //概述：根据初始化容器的长度与最新的容器的长度进行比对，就可以推导出当前 SpiderEngine 集群的状态：新增，宕机，变更...

            //思想：哪个容器中元素多，就循环遍历哪个容器。

            //新增
            if (newZodeInfos.size()>initAllZnodes.size()){
                //明确显示新增了哪个 SpiderEngine 节点
                for (String nowZNode:newZodeInfos) {
                    if (!newZodeInfos.contains(nowZNode)){
                        log.info("新增 SpiderEngine 节点{}", nowZNode);
                    }
                }
            }else if (newZodeInfos.size()<initAllZnodes.size()){
                //宕机
                //明确显示哪个 SpiderEngine 节点宕机了
                for (String initZNode : initAllZnodes) {
                    if (!newZodeInfos.contains(initZNode)) {
                        log.info("SpiderEngine 节点【{}】宕机了！", initZNode);

                        // 宕机的处理
                        if (downTimeProcess!=null) {
                            downTimeProcess.process();
                        }
                    }
                }

            }else {
                // SpiderEngine 的个数未发生变化（不用处理）
                //①爬虫集群正常运行
                //②宕机了，当时马上重启了，总的爬虫未发生变化
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //要达到每次都是与上一次比较的效果，需要动态替换：initAllZnodes
        initAllZnodes = newZodeInfos;
    }

    private void start(){
        while (true){

        }
    }

    /**
     * SpiderEngine 节点宕机的处理
     */
    @FunctionalInterface
    public interface DownTimeProcess  {

        void process();
    }

    public static void main(String[] args) {
        //监控服务启动
        new CuratorManager().start();
    }
}
