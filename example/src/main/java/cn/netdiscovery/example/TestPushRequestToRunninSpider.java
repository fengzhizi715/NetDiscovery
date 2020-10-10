package cn.netdiscovery.example;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.disruptor.DisruptorQueue;

import java.util.concurrent.CompletableFuture;

/**
 * Created by tony on 2019-04-25.
 */
public class TestPushRequestToRunninSpider {

    public static void main(String[] args) {

        Spider spider = Spider.create(new DisruptorQueue())
                .name("tony")
                .url("http://www.163.com");

        CompletableFuture.runAsync(()->{
            spider.run();
        });

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        spider.getQueue().pushToRunninSpider(new Request("https://www.baidu.com", "tony"),spider);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        spider.getQueue().pushToRunninSpider(new Request("https://www.jianshu.com", "tony"),spider);

        System.out.println("end....");
    }
}
