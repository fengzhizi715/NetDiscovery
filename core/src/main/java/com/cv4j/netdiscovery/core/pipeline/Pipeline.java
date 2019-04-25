package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.safframework.tony.common.utils.Preconditions;

import java.util.Map;

/**
 * Created by tony on 2017/12/22.
 */
public interface Pipeline {

    void process(ResultItems resultItems);

    /**
     * 方便在 pipeline 中往队列中发起爬取任务(进行深度爬取)
     * @param spider
     * @param url
     */
    default void push(Spider spider, String url) {

        if (spider==null || Preconditions.isBlank(url)) {
            return;
        }

        Request request = new Request(url,spider.getName());     // 根据spider的名称来创建request
        spider.getQueue().push(request);
    }

    /**
     * 方便在 pipeline 中往队列中发起爬取任务(进行深度爬取)
     * @param spider
     * @param originalRequest 原始的request，新的request可以继承原始request的header信息
     * @param url
     */
    default void push(Spider spider, Request originalRequest, String url) {

        if (spider==null || originalRequest==null || Preconditions.isBlank(url)) {
            return;
        }

        Request request = new Request(url,spider.getName());     // 根据spider的名称来创建request
        Map<String,String> header = originalRequest.getHeader(); // 从原始request中获取header
        if (Preconditions.isNotBlank(header)) {                  // 将原始request的header复制到新的request

            header.forEach((key,value)->{

                request.header(key,value);
            });
        }

        spider.getQueue().push(request);
    }
}
