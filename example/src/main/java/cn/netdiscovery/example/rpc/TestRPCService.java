package cn.netdiscovery.example.rpc;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.SpiderEngine;
import cn.netdiscovery.core.verticle.RPCVerticle;

/**
 * @FileName: cn.netdiscovery.example.rpc.TestRPCService
 * @author: Tony Shen
 * @date: 2020-03-10 11:47
 * @version: V1.0 <描述当前版本功能>
 */
public class TestRPCService {

    public static void main(String[] args) {

        SpiderEngine engine = SpiderEngine.create();

        Spider spider = Spider.create()
                .name("tony1")
                .repeatRequest(10000,"http://www.163.com")
                .initialDelay(10000);

        engine.addSpider(spider);

        Spider spider2 = Spider.create()
                .name("tony2")
                .repeatRequest(10000,"https://www.baidu.com")
                .initialDelay(10000);

        engine.addSpider(spider2);

        Spider spider3 = Spider.create()
                .name("tony3")
                .repeatRequest(10000,"http://www.126.com")
                .initialDelay(10000);

        engine.addSpider(spider3);

        engine.addSpider(Spider.create().name("tony").url("https://www.jianshu.com/"));

        engine.setUseMonitor(true);
        engine.deployVerticle(new RPCVerticle(engine.getSpiders(),engine.getJobs()));
        engine.httpd(8081);
        engine.run();
    }
}
