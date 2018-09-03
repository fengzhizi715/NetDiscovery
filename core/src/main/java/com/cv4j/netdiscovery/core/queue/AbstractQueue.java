package com.cv4j.netdiscovery.core.queue;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.filter.DuplicateFilter;
import com.cv4j.netdiscovery.core.queue.filter.HashSetDuplicateFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by tony on 2018/1/3.
 */
@Slf4j
public abstract class AbstractQueue implements Queue {

    private DuplicateFilter filter = new HashSetDuplicateFilter();

    public DuplicateFilter getFilter() {
        return filter;
    }

    public AbstractQueue setFilter(DuplicateFilter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public void push(Request request) {
        log.debug("get a candidate url {}", request.getUrl());
        if (!filter.isDuplicate(request)) {
            log.debug("push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request);
        }
    }

    protected abstract void pushWhenNoDuplicate(Request request);

    public int getTotalRequests(String spiderName) {

        return getFilter().getTotalRequestsCount();
    }
}
