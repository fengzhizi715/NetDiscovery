package com.cv4j.netdiscovery.core.queue.disruptor;

import com.cv4j.netdiscovery.core.domain.Request;
import com.lmax.disruptor.RingBuffer;

/**
 * Created by tony on 2018/9/2.
 */
public class Producer {

    private final RingBuffer<RequestEvent> ringBuffer;

    public Producer(RingBuffer<RequestEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void pushData(Request request){
        long sequence = ringBuffer.next();
        try{
            RequestEvent event = ringBuffer.get(sequence);
            event.setRequest(request);
        }finally {
            ringBuffer.publish(sequence);
        }
    }
}
