package cn.netdiscovery.example.etcd;

import cn.netdiscovery.core.watch.etcd.EtcdManager;

/**
 * Created by tony on 2019-06-09.
 */
public class TestEtcdManager {

    public static void main(String[] args) {

        EtcdManager etcdManager = new EtcdManager();
        etcdManager.httpd().start();
    }
}
