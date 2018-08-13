package com.cv4j.netdiscovery.core.utils;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

/**
 * Created by tony on 2018/8/13.
 */
@Slf4j
public class RetryWithDelay implements Function<Flowable<Throwable>, Publisher> {

    private int retryCount=0;
    private int maxRetries;
    private int retryDelayMillis;

    public RetryWithDelay(int maxRetries,int retryDelayMillis) {

        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Publisher apply(Flowable<Throwable> attempts) throws Exception {
        return attempts.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Throwable throwable) throws Exception {
                if (++retryCount <= maxRetries) {

                    log.info("get error, it will try after " + retryDelayMillis
                            + " millisecond, retry count " + retryCount);
                    // When this Observable calls onNext, the original
                    // Observable will be retried (i.e. re-subscribed).
                    return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);

                } else {

                    // Max retries hit. Just pass the error along.
                    return Flowable.error(throwable);
                }
            }
        });
    }
}
