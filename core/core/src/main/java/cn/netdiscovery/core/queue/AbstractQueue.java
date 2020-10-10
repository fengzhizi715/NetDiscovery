package cn.netdiscovery.core.queue;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.filter.DuplicateFilter;
import cn.netdiscovery.core.queue.filter.HashSetDuplicateFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tony on 2018/1/3.
 */
public abstract class AbstractQueue implements Queue {

    private Logger log = LoggerFactory.getLogger(AbstractQueue.class);

    private DuplicateFilter filter = new HashSetDuplicateFilter();

    public DuplicateFilter getFilter() {
        return filter;
    }

    public void setFilter(DuplicateFilter filter) {
        this.filter = filter;
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

    @Override
    public int getTotalRequests(String spiderName) {

        return getFilter().getTotalRequestsCount();
    }
}
