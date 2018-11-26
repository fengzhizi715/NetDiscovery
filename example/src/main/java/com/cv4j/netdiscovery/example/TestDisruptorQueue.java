package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.disruptor.DisruptorQueue;

/**
 * Created by tony on 2018/11/26.
 */
public class TestDisruptorQueue {

    public static void main(String[] args) {

        Request request1 = new Request("http://www.163.com");

        Request request2 = new Request("https://www.baidu.com/");

        Request request3 = new Request("https://www.jianshu.com/u/4f2c483c12d8");

        Spider.create(new DisruptorQueue())
                .name("tony")
                .request(request1,request2,request3)
                .run();


    }
}
