package com.cv4j.netdiscovery.core.queue.disruptor;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2018/9/1.
 */
public class DisruptorQueue extends AbstractQueue {

    private RingBuffer<RequestEvent> ringBuffer;
    private Consumer consumer = null;
    private Producer producer = null;
    private AtomicInteger count = new AtomicInteger(0);

    public DisruptorQueue() {

        int ringBufferSize = 1024*1024; // RingBuffer 大小，必须是 2 的 N 次方；

        //创建ringBuffer
        ringBuffer = RingBuffer.create(ProducerType.MULTI,
                        new EventFactory<RequestEvent>() {
                            @Override
                            public RequestEvent newInstance() {
                                return new RequestEvent();
                            }
                        },
                        ringBufferSize ,
                        new YieldingWaitStrategy());

        SequenceBarrier barriers = ringBuffer.newBarrier();

        consumer = new Consumer();

        WorkerPool<RequestEvent> workerPool =
                new WorkerPool<RequestEvent>(ringBuffer,
                        barriers,
                        new IntEventExceptionHandler(),
                        consumer);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(Executors.newSingleThreadExecutor());

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

        Request request = ringBuffer.get(ringBuffer.getCursor()-getTotalRequests(spiderName)+1).getRequest();
        ringBuffer.next();
        count.incrementAndGet();
        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {
        return getTotalRequests(spiderName) - count.get()+1;
    }

    public int getTotalRequests(String spiderName) {

        return consumer.getCount();
    }

    static class IntEventExceptionHandler implements ExceptionHandler {
        public void handleEventException(Throwable ex, long sequence, Object event) {}
        public void handleOnStartException(Throwable ex) {}
        public void handleOnShutdownException(Throwable ex) {}
    }
}
