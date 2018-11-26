package com.cv4j.netdiscovery.core.queue.disruptor;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2018/9/1.
 */
@Slf4j
public class DisruptorQueue extends AbstractQueue {

    private RingBuffer<RequestEvent> ringBuffer;

    private Consumer[] consumers = new Consumer[2];
    private Producer producer = null;
    private int ringBufferSize = 1024*1024; // RingBuffer 大小，必须是 2 的 N 次方；

    private AtomicInteger count = new AtomicInteger(0);
    private AtomicInteger consumerCount = new AtomicInteger(0);

    public DisruptorQueue() {

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

        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer();
        }

        WorkerPool<RequestEvent> workerPool =
                new WorkerPool<RequestEvent>(ringBuffer,
                        barriers,
                        new EventExceptionHandler(),
                        consumers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(Executors.newFixedThreadPool(4));

        producer = new Producer(ringBuffer);
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        producer.pushData(request);
        count.incrementAndGet();
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
        consumerCount.incrementAndGet();
        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return getTotalRequests(spiderName)-consumerCount.get();
    }

    public int getTotalRequests(String spiderName) {

        return count.get();
    }

    static class EventExceptionHandler implements ExceptionHandler {

        public void handleEventException(Throwable ex, long sequence, Object event) {

            log.debug("handleEventException：" + ex);
        }

        public void handleOnStartException(Throwable ex) {

            log.debug("handleOnStartException：" + ex);
        }

        public void handleOnShutdownException(Throwable ex) {

            log.debug("handleOnShutdownException：" + ex);
        }
    }
}
