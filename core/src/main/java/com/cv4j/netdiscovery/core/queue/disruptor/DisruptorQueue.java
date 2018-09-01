package com.cv4j.netdiscovery.core.queue.disruptor;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tony on 2018/9/1.
 */
public class DisruptorQueue extends AbstractQueue {

    private Disruptor<RequestEvent> disruptor = null;
    private RingBuffer<RequestEvent> ringBuffer;
    private Producer producer = null;

    public DisruptorQueue() {

        EventFactory<RequestEvent> eventFactory = new RequestEventFactory();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int ringBufferSize = 1024; // RingBuffer 大小，必须是 2 的 N 次方；

        disruptor = new Disruptor<RequestEvent>(eventFactory,ringBufferSize,executor,
                ProducerType.MULTI,new BlockingWaitStrategy());

        disruptor.handleEventsWithWorkerPool(new Consumer());
        disruptor.start();

        ringBuffer = disruptor.getRingBuffer();
        producer = new Producer(ringBuffer);
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        producer.pushData(request);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request poll(String spiderName) {

        Request request = disruptor.get(disruptor.getCursor()-getTotalRequests(spiderName)+1).getRequest();
        disruptor.getRingBuffer().next();
        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {
        return 0;
    }

    @Override
    public int getTotalRequests(String spiderName) {
        return getFilter().getTotalRequestsCount();
    }
}
