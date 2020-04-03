package cn.netdiscovery.core.downloader;

import cn.netdiscovery.core.cache.RxCacheManager;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import com.safframework.rxcache.RxCache;
import io.reactivex.Maybe;

import java.io.Closeable;

/**
 * Created by tony on 2017/12/23.
 */
public interface Downloader extends Closeable {

    Maybe<Response> download(Request request);

    /**
     * 将爬取的内容，存到 RxCache 中
     * @param key
     * @param response
     */
    default void save(String key, Response response) {

        if (RxCacheManager.getInstance().getRxCache()==null || !RxCacheManager.getInstance().getRxCache().test()) { // 如果cache为空或者cache不可用，则使用默认的配置

            RxCacheManager.getInstance().config(new RxCache.Builder());
        }

        RxCacheManager.getInstance().getRxCache().save(key,response);
    }
}
