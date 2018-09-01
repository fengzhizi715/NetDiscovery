package com.cv4j.netdiscovery.core.queue.disruptor;

import com.cv4j.netdiscovery.core.domain.Request;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by tony on 2018/9/2.
 */
public class Consumer implements WorkHandler<RequestEvent> {

    @Override
    public void onEvent(RequestEvent requestEvent) throws Exception {
    }
}
