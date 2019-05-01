package cn.netdiscovery.core.queue.disruptor;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by tony on 2018/9/2.
 */
@Slf4j
public class Consumer implements WorkHandler<RequestEvent> {

    @Override
    public void onEvent(RequestEvent requestEvent) throws Exception {

        log.info("consumer:" + Thread.currentThread().getName() + " requestEvent: value=" + requestEvent.toString());
    }
}
