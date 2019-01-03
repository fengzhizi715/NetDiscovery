package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.cache.RxCacheManager;
import com.cv4j.netdiscovery.core.domain.ResultItems;

import java.util.Map;

/**
 * Created by tony on 2019-01-03.
 */
public class RxCachePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            RxCacheManager.getRxCache().save(entry.getKey(), entry.getValue()); // 缓存的对象需要实现序列化
        }
    }
}
