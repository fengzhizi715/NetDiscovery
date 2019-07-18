package cn.netdiscovery.core.queue;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;

/**
 * Created by tony on 2018/1/1.
 */
public interface Queue {

    /**
     * 把url请求添加到正在运行爬虫的Queue中，无需阻塞爬虫的运行
     * @param url
     * @param spider
     */
    default void pushToRunninSpider(String url, Spider spider) {

        pushToRunninSpider(new Request(url,spider.getName()),spider);
    }

    /**
     * 把Request请求添加到正在运行爬虫的Queue中，无需阻塞爬虫的运行
     *
     * @param request request
     */
    default void pushToRunninSpider(Request request, Spider spider) {

        push(request);
        spider.signalNewRequest();
    }

    /**
     * 把Request请求添加到Queue
     *
     * @param request request
     */
    void push(Request request);

    /**
     * 从Queue中取出一个Request
     *
     * @param spiderName
     * @return the request to spider
     */
    Request poll(String spiderName);

    /**
     * Queue中还剩下多少Request没有消费
     *
     * @param spiderName
     * @return
     */
    int getLeftRequests(String spiderName);

    /**
     * Queue中总共的Request
     *
     * @param spiderName
     * @return
     */
    int getTotalRequests(String spiderName);
}
