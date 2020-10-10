package cn.netdiscovery.core.pipeline;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.ResultItems;
import com.safframework.tony.common.utils.Preconditions;

import java.util.Map;

/**
 * Created by tony on 2017/12/22.
 */
public abstract class Pipeline {

    private long pipelineDelay = 0;  // 默认0s

    public Pipeline() {
    }

    public Pipeline(long pipelineDelay) {

        if (pipelineDelay>0) {
            this.pipelineDelay = pipelineDelay;
        }
    }

    public long getPipelineDelay() {
        return pipelineDelay;
    }

    public void setPipelineDelay(long pipelineDelay) {
        this.pipelineDelay = pipelineDelay;
    }

    public abstract void process(ResultItems resultItems);

    /**
     * 方便在 pipeline 中往队列中发起爬取任务(进行深度爬取)
     * @param spider
     * @param url
     */
    public void push(Spider spider, String url) {

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
    public void push(Spider spider, Request originalRequest, String url) {

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
