package com.cv4j.netdiscovery.core.queue;

import com.cv4j.netdiscovery.core.http.Request;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 线程安全的非阻塞FIFO队列
 *
 * Created by tony on 2018/1/1.
 */
public class DefaultQueue implements Queue {

    private ConcurrentLinkedQueue<Request> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void push(Request request,String spiderName) {

        queue.offer(request);
    }

    @Override
    public Request poll(String spiderName) {

        return queue.poll();
    }
}
