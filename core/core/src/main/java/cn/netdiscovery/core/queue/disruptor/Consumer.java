package cn.netdiscovery.core.queue.disruptor;

import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tony on 2018/9/2.
 */
public class Consumer implements WorkHandler<RequestEvent> {

    private Logger log = LoggerFactory.getLogger(Consumer.class);

    @Override
    public void onEvent(RequestEvent requestEvent) throws Exception {

        log.info("consumer:" + Thread.currentThread().getName() + " requestEvent: value=" + requestEvent.toString());
    }
}
