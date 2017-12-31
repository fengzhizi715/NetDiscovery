package com.cv4j.netdiscovery.core.scheduler;

import com.cv4j.netdiscovery.core.http.Request;

/**
 * Created by tony on 2017/12/31.
 */
public interface Scheduler {

    /**
     * add a url to fetch
     *
     * @param request request
     */
    void push(Request request);

    /**
     * get an url to crawl
     *
     * @return the url to spider
     */
    Request poll();
}
