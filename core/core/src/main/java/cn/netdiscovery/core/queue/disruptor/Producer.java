package cn.netdiscovery.core.queue.disruptor;

import cn.netdiscovery.core.domain.Request;
import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2018/9/2.
 */
public class Producer {

    private final RingBuffer<RequestEvent> ringBuffer;

    private AtomicInteger count = new AtomicInteger(0); // 计数器

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
            count.incrementAndGet();
        }
    }

    /**
     * 发送到队列中到Request的数量
     * @return
     */
    public int getCount() {

        return count.get();
    }
}
