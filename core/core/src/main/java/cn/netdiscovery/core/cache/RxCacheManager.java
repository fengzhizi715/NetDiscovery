package cn.netdiscovery.core.cache;

import com.safframework.rxcache.RxCache;

/**
 * Created by tony on 2019-01-03.
 */
public class RxCacheManager {

    private RxCache cache;

    private static class Holder {
        private static final RxCacheManager instance = new RxCacheManager();
    }

    private RxCacheManager() {
    }

    public static final RxCacheManager getInstance() {
        return RxCacheManager.Holder.instance;
    }

    /**
     * 配置RxCache.Builder
     * @param builder
     */
    public void config(RxCache.Builder builder) {

        RxCache.config(builder);
        cache = RxCache.getRxCache();
    }

    public RxCache getRxCache() {

        return cache;
    }
}
