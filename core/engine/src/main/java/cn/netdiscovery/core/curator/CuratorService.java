package cn.netdiscovery.core.curator;

import cn.netdiscovery.core.config.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by tony on 2019-05-21.
 */
@Slf4j
public class CuratorService implements Watcher {

    private CuratorFramework client;

    /**
     * 容器，用于存储指定zNode下所有子zNode的名字
     */
    private List<String> initAllZnodes;

    public CuratorService() {

        String zkStr = Configuration.getConfig("spiderEngine.config.zkStr");

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

    /**
     * 当前所监控的父的zNode下若是子zNode发生了变化：新增，删除，修改
     * <p>
     * 下述方法都会触发执行
     *
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        //System.out.println("process执行了哦。。。。");
        List<String> newZodeInfos = null;
        try {
            newZodeInfos = client.getChildren().usingWatcher(this).forPath("/netdiscovery");
            //概述：根据初始化容器的长度与最新的容器的长度进行比对，就可以推导出当前爬虫集群的状态：新增，宕机，变更...

            //思想：哪个容器中元素多，就循环遍历哪个容器。

            //新增
            if (newZodeInfos.size()>initAllZnodes.size()){
                //明确显示新增了哪个爬虫节点
                for (String nowZNode:newZodeInfos) {
                    if (!newZodeInfos.contains(nowZNode)){
                        // System.out.printf("新增爬虫节点【%s】%n", nowZNode);
                        log.info("新增爬虫节点{}", nowZNode);
                    }
                }
            }else if (newZodeInfos.size()<initAllZnodes.size()){
                //宕机
                //明确显示哪个爬虫节点宕机了
                for (String initZNode : initAllZnodes) {
                    if (!newZodeInfos.contains(initZNode)) {
                        log.info("爬虫节点【{}】宕机了哦！", initZNode);

//                        //分布式爬虫的HA
//                        Process ps = Runtime.getRuntime().exec("/opt/crawler/crawler.sh");
//                        ps.waitFor();
//                        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
//                        String line = null;
//                        while ((line = br.readLine()) != null) {
//                            log.info(line);
//                        }
                    }
                }

            }else {
                //容器中爬虫的个数未发生变化（不用处理）
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

    public static void main(String[] args) {
        //监控服务启动
        new CuratorService().start();
    }
}
