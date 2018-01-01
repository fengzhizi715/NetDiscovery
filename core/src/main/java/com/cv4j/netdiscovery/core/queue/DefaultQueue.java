package com.cv4j.netdiscovery.core.queue;

import com.cv4j.netdiscovery.core.http.Request;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by tony on 2018/1/1.
 */
public class DefaultQueue implements Queue {

    private ConcurrentLinkedQueue<Request> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void push(Request request) {
        queue.offer(request);
    }

    @Override
    public Request poll() {
        Request request = queue.poll();
        return request;
    }
}
