package cn.netdiscovery.core.rxjava;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.utils.SpiderUtils;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

/**
 * Created by tony on 2018/8/13.
 */
@Slf4j
public class RetryWithDelay<T> implements Function<Flowable<Throwable>, Publisher<T>> {

    private int retryCount = 0;
    private int maxRetries;
    private long retryDelayMillis;
    private Request request;

    public RetryWithDelay(int maxRetries, long retryDelayMillis, Request request) {

        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.request = request;
    }

    @Override
    public Publisher apply(Flowable<Throwable> attempts) throws Exception {
        return attempts.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Throwable throwable) throws Exception {
                if (++retryCount <= maxRetries && request!=null) {

                    String url = request.getUrl();

                    if (Preconditions.isNotBlank(url)) {

                        log.info("url:" + url + " get error, it will try after " + retryDelayMillis
                                + " millisecond, retry count " + retryCount);

                        log.info(request.toString());
                    } else {

                        log.info("get error, it will try after " + retryDelayMillis
                                + " millisecond, retry count " + retryCount);

                        return Flowable.error(throwable);
                    }

                    return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
                            .map(new Function<Long, Long>() {
                                @Override
                                public Long apply(Long aLong) throws Exception {

                                    Request.BeforeRequest beforeRequest = request.getBeforeRequest();

                                    Proxy proxy = ProxyPool.getProxy();

                                    if (proxy != null && SpiderUtils.checkProxy(proxy)) { // 如果存在代理，则重试时切换一下代理
                                        request.proxy(proxy);
                                    }

                                    if (beforeRequest != null) {
                                        beforeRequest.process(request);
                                    }
                                    return aLong;
                                }
                            });
                }

                Request.OnErrorRequest onErrorRequest = request.getOnErrorRequest();
                if (onErrorRequest != null) {
                    onErrorRequest.process(request);
                }

                // Max retries hit. Just pass the error along.
                return Flowable.error(throwable);
            }
        });
    }
}
