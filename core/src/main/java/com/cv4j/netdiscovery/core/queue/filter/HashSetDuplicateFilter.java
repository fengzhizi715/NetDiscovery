package com.cv4j.netdiscovery.core.queue.filter;

import com.cv4j.netdiscovery.core.http.Request;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tony on 2018/1/3.
 */
public class HashSetDuplicateFilter implements DuplicateFilter {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request) {

        return !urls.add(request.getUrl());
    }
}
