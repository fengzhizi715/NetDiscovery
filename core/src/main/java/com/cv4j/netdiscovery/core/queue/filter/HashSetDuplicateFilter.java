package com.cv4j.netdiscovery.core.queue.filter;

import com.cv4j.netdiscovery.core.http.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tony on 2018/1/3.
 */
public class HashSetDuplicateFilter implements DuplicateFilter {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private List<String> noNeedCheckDuplicateUrls = new ArrayList<>();

    @Override
    public boolean isDuplicate(Request request) {

        if (request.isCheckDuplicate()) {

            return !urls.add(request.getUrl());
        } else {

            noNeedCheckDuplicateUrls.add(request.getUrl());
            return false;
        }

    }

    @Override
    public int getTotalRequestsCount() {

        return urls.size() + noNeedCheckDuplicateUrls.size();
    }
}
