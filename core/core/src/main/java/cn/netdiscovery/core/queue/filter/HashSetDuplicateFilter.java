package cn.netdiscovery.core.queue.filter;

import cn.netdiscovery.core.domain.Request;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tony on 2018/1/3.
 */
public class HashSetDuplicateFilter implements DuplicateFilter {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private List<String> noNeedCheckDuplicateUrls = new CopyOnWriteArrayList<>();

    @Override
    public boolean isDuplicate(Request request) {

        if (request.getCheckDuplicate()) {

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
