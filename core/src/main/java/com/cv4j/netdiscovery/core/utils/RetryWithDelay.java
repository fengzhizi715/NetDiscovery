package com.cv4j.netdiscovery.core.utils;

import com.cv4j.netdiscovery.core.domain.Request;
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
    private int retryDelayMillis;
    private Request request;

    public RetryWithDelay(int maxRetries, int retryDelayMillis) {

        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    public RetryWithDelay(int maxRetries, int retryDelayMillis, Request request) {

        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.request = request;
    }

    @Override
    public Publisher apply(Flowable<Throwable> attempts) throws Exception {
        return attempts.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Throwable throwable) throws Exception {
                if (++retryCount <= maxRetries) {

                    if (Preconditions.isNotBlank(request.getUrl())) {

                        log.info("url:" + request.getUrl() + " get error, it will try after " + retryDelayMillis
                                + " millisecond, retry count " + retryCount);
                    } else {

                        log.info("get error, it will try after " + retryDelayMillis
                                + " millisecond, retry count " + retryCount);
                    }

                    // Redo beforeRequest.
                    request.getBeforeRequest().process(request);

                    return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);

                } else {

                    // Max retries hit. Just pass the error along.
                    return Flowable.error(throwable);
                }
            }
        });
    }
}
