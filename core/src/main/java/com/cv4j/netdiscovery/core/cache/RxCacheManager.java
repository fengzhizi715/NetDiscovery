package com.cv4j.netdiscovery.core.cache;

import com.safframework.rxcache.RxCache;

/**
 * Created by tony on 2019-01-03.
 */
public final class RxCacheManager {

    private static RxCache cache = null;

    /**
     * 配置RxCache.Builder
     * @param builder
     */
    public static void config(RxCache.Builder builder) {

        RxCache.config(builder);
        cache = RxCache.getRxCache();
    }

    public static RxCache getRxCache() {

        return cache;
    }
}
