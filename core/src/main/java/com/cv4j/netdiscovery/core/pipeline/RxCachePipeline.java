package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.cache.RxCacheManager;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.safframework.rxcache.RxCache;

/**
 * Created by tony on 2019-01-03.
 */
public class RxCachePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        RxCache rxCache = RxCacheManager.getInsatance().getRxCache();

        if (rxCache==null || !rxCache.test()) { // 如果cache为空或者cache不可用，则使用默认的配置

            RxCacheManager.getInsatance().config(new RxCache.Builder());
        }

        resultItems.getAll().forEach((key,value)->{
            rxCache.save(key,value); // 缓存的对象需要实现序列化
        });
    }
}
