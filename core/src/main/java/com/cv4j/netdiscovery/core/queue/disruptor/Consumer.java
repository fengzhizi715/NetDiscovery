package com.cv4j.netdiscovery.core.queue.disruptor;

import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2018/9/2.
 */
public class Consumer implements WorkHandler<RequestEvent> {

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void onEvent(RequestEvent requestEvent) throws Exception {

        count.incrementAndGet();
    }

    public int getCount(){
        return count.get();
    }
}
