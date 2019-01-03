package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.cache.RxCacheManager;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.safframework.rxcache.RxCache;

import java.util.Map;

/**
 * Created by tony on 2019-01-03.
 */
public class RxCachePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        RxCache rxCache = RxCacheManager.getInsatance().getRxCache();
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            rxCache.save(entry.getKey(), entry.getValue()); // 缓存的对象需要实现序列化
        }
    }
}
