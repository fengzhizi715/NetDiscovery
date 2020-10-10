package cn.netdiscovery.core.queue.disruptor;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.AbstractQueue;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2018/9/1.
 */
public class DisruptorQueue extends AbstractQueue {

    private static Logger log = LoggerFactory.getLogger(DisruptorQueue.class);

    private RingBuffer<RequestEvent> ringBuffer;
    private Producer producer = null;

    private AtomicInteger consumerCount = new AtomicInteger(0);

    private static final int CONSUME_NUM = 2;
    private static final int THREAD_NUM = 4;
    private static final int RING_BUFFER_SIZE = 1024*1024; // RingBuffer 大小，必须是 2 的 N 次方

    public DisruptorQueue() {

        this(CONSUME_NUM,THREAD_NUM);
    }

    public DisruptorQueue(int consumerNum,int threadNum) {

        this(consumerNum,threadNum,RING_BUFFER_SIZE);
    }

    /**
     *
     * @param consumerNum
     * @param threadNum
     * @param ringBufferSize RingBuffer 大小，必须是 2 的 N 次方
     */
    public DisruptorQueue(int consumerNum,int threadNum,int ringBufferSize) {

        Consumer[] consumers = new Consumer[consumerNum];

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

        WorkerPool<RequestEvent> workerPool = new WorkerPool<RequestEvent>(ringBuffer,
                        barriers,
                        new EventExceptionHandler(),
                        consumers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(Executors.newFixedThreadPool(threadNum));

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

        Request request = ringBuffer.get(ringBuffer.getCursor() - producer.getCount() +1).getRequest();
        ringBuffer.next();
        consumerCount.incrementAndGet();
        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return producer.getCount()-consumerCount.get();
    }

    @Override
    public int getTotalRequests(String spiderName) {

        return super.getTotalRequests(spiderName);
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
