package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.queue.Queue;
import com.safframework.tony.common.utils.Preconditions;

import java.util.Map;

/**
 * Created by tony on 2017/12/22.
 */
public interface Pipeline {

    void process(ResultItems resultItems);

    /**
     * 方便在 pipeline 中往队列中发起爬取任务
     * @param spider
     * @param queue 使用的queue
     * @param originalRequest 原始的request，新的request可以继承原始request的header信息
     * @param url
     */
    default void push(Spider spider, Queue queue, Request originalRequest, String url) {

        Request request = new Request(url);
        Map<String,String> header = originalRequest.getHeader(); // 从原始request中获取header
        if (Preconditions.isNotBlank(header)) {

            header.forEach((key,value)->{

                request.header(key,value);
            });
        }

        if (queue.isEmpty(originalRequest.getSpiderName())) {

            queue.push(request);
            spider.run(); // 重启爬虫
        } else {
            queue.push(request);
        }
    }
}
