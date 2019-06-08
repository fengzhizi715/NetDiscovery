package cn.netdiscovery.core.registry;

import cn.netdiscovery.core.config.Configuration;
import cn.netdiscovery.core.config.Constant;
import com.safframework.tony.common.utils.Preconditions;
import lombok.Getter;
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
@Getter
public class ZKRegistry implements Registry {

    private String zkStr;
    private String zkPath;

    public ZKRegistry() {

        zkStr = Configuration.getConfig("spiderEngine.registry.zookeeper.zkStr");
        zkPath = Configuration.getConfig("spiderEngine.registry.zookeeper.zkPath");
    }

    @Override
    public void register(String connectString, String path,int port) {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        client.start();

        try {
            if (Preconditions.isBlank(path)) {
                path = Constant.DEFAULT_PATH;
            }

            Stat stat = client.checkExists().forPath(path);

            if (stat==null) { // 如果父目录不存在，则事先创建父目录
                client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }

            String ipAddr = InetAddress.getLocalHost().getHostAddress() + "-" + port + "-" + System.currentTimeMillis();
            String nowSpiderEngineZNode = path + "/" + ipAddr;
            client.create().withMode(CreateMode.EPHEMERAL).forPath(nowSpiderEngineZNode,nowSpiderEngineZNode.getBytes());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
