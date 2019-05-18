package cn.netdiscovery.core.queue.filter;

import cn.netdiscovery.core.domain.Request;

/**
 * Created by tony on 2018/1/3.
 */
public interface DuplicateFilter {

    /**
     *
     * Check whether the request is duplicate.
     *
     * @param request request
     * @return true if is duplicate
     */
    boolean isDuplicate(Request request);

    /**
     * Get TotalRequestsCount for monitor.
     * @return number of total request
     */
    int getTotalRequestsCount();
}
