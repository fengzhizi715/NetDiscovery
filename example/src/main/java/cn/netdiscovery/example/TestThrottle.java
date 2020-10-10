package cn.netdiscovery.example;

import cn.netdiscovery.core.Spider;

/**
 * Created by tony on 2019-05-09.
 */
public class TestThrottle {

    public static void main(String[] args) {

        Request request1 = new Request("http://www.163.com","tony").checkDuplicate(false);

        Request request2 = new Request("http://www.163.com","tony").checkDuplicate(false).domainDelay(5000);

        Spider.create().name("tony1")
                .request(request1,request2)
                .run();
    }
}
