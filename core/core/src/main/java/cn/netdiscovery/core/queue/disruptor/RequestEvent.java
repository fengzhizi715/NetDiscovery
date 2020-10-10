package cn.netdiscovery.core.queue.disruptor;

import cn.netdiscovery.core.domain.Request;

/**
 * Created by tony on 2018/9/1.
 */
public class RequestEvent {

    private Request request;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String toString() {

        return request.toString();
    }
}
