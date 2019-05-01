package com.cv4j.netdiscovery.core.queue;

import com.cv4j.netdiscovery.core.domain.Request;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by tony on 2018/6/19.
 */
public class PriorityQueue extends AbstractQueue {

    private int capacity = 20;

    private ConcurrentLinkedQueue<Request> normalQueue = new ConcurrentLinkedQueue<>();

    private PriorityBlockingQueue<Request> priorityQueue = new PriorityBlockingQueue<>(capacity, (Request o1, Request o2) -> {
        if (o1.getPriority() > o2.getPriority()) {

            return -1;
        } else if (o1.getPriority() == o2.getPriority()) {

            return 0;
        } else {

            return 1;
        }
    });

    public PriorityQueue() {
    }

    public PriorityQueue(int capacity) {

        this.capacity = capacity;
    }
    
    @Override
    protected void pushWhenNoDuplicate(Request request) {

        if (request.getPriority() == 0) {

            normalQueue.add(request);
        } else if (request.getPriority() > 0) {

            priorityQueue.put(request);
        }
    }

    @Override
    public synchronized Request poll(String spiderName) {

        Request request = priorityQueue.poll(); // 先从优先级队列里取request

        if (request!=null) {

            return request;
        }

        return normalQueue.poll(); // 优先级队列里没有request 再从normalQueue中取
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return normalQueue.size() + priorityQueue.size();
    }
}
