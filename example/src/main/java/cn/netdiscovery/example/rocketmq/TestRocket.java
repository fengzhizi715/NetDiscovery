package cn.netdiscovery.example.rocketmq;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.queue.rocketmq.Consumer;
import cn.netdiscovery.queue.rocketmq.Producer;
import cn.netdiscovery.queue.rocketmq.RocketQueue;

/**
 * Created by tony on 2019-07-23.
 */
public class TestRocket {

    public static void main(String[] args) {

        Producer producer = new Producer("test_producer","10.184.16.17:9876",3,"test_tag");
        Consumer consumer = new Consumer("test_consumer","10.184.16.17:9876");

        RocketQueue queue = new RocketQueue(producer,consumer);

        Request request1 = new Request("https://www.baidu.com").checkDuplicate(false);
        Request request2 = new Request("https://www.163.com").checkDuplicate(false);

        Spider.create(queue)
                .name("tony")
                .request(request1,request2)
                .run();
    }
}
