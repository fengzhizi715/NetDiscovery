package com.cv4j.netdiscovery.core.downloader;

import com.cv4j.netdiscovery.core.cache.RxCacheManager;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.safframework.rxcache.RxCache;
import io.reactivex.Maybe;

import java.io.Closeable;

/**
 * Created by tony on 2017/12/23.
 */
public interface Downloader extends Closeable {

    Maybe<Response> download(Request request);

    default void save(String key, Response response) {

        if (RxCacheManager.getInsatance().getRxCache()==null || !RxCacheManager.getInsatance().getRxCache().test()) { // 如果cache为空或者cache不可用，则使用默认的配置

            RxCacheManager.getInsatance().config(new RxCache.Builder());
        }

        RxCacheManager.getInsatance().getRxCache().save(key,response);
    }
}
