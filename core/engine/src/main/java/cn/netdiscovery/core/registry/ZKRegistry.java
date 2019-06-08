package cn.netdiscovery.core.registry;

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
 * Created by tony on 2019-06-09.
 */
public class ZKRegistry implements Registry {

    @Override
    public void register(String connectString, String path,int port) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        client.start();

        try {
            if (Preconditions.isBlank(path)) {
                path = "/netdiscovery";
            }

            Stat stat = client.checkExists().forPath(path);

            if (stat==null) {
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
