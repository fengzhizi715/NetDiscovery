package cn.netdiscovery.queue.rocketmq;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.AbstractQueue;

/**
 * Created by tony on 2019-07-16.
 */
public class RocketQueue extends AbstractQueue {

    @Override
    protected void pushWhenNoDuplicate(Request request) {

    }

    @Override
    public Request poll(String spiderName) {
        return null;
    }

    @Override
    public int getLeftRequests(String spiderName) {
        return 0;
    }
}
