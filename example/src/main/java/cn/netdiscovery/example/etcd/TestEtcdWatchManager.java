package cn.netdiscovery.example.etcd;

import cn.netdiscovery.core.watch.etcd.EtcdWatchManager;

/**
 * Created by tony on 2019-06-09.
 */
public class TestEtcdWatchManager {

    public static void main(String[] args) {

        EtcdWatchManager etcdManager = new EtcdWatchManager();
        etcdManager.httpd().start();
    }
}
