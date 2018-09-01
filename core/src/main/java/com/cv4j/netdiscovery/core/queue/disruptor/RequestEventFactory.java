package com.cv4j.netdiscovery.core.queue.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by tony on 2018/9/1.
 */
public class RequestEventFactory implements EventFactory<RequestEvent> {

    @Override
    public RequestEvent newInstance() {
        return new RequestEvent();
    }
}
