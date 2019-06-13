package cn.netdiscovery.example.zookeeper;

import cn.netdiscovery.core.watch.zookeeper.ZooKeeperWatchManager;

/**
 * Created by tony on 2019-05-25.
 */
public class TestZooKeeperWatchManager {

    public static void main(String[] args) {

        ZooKeeperWatchManager curatorManager = new ZooKeeperWatchManager();
        curatorManager.httpd().start();
    }
}
