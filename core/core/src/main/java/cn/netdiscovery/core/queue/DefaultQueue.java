package cn.netdiscovery.core.queue;

import cn.netdiscovery.core.domain.Request;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 线程安全的非阻塞FIFO队列
 *
 * Created by tony on 2018/1/1.
 */
public class DefaultQueue extends AbstractQueue {

    private ConcurrentLinkedQueue<Request> queue = new ConcurrentLinkedQueue<>();

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        queue.offer(request);
    }

    @Override
    public Request poll(String spiderName) {

        return queue.poll();
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return queue.size();
    }
}
