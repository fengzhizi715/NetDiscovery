package cn.netdiscovery.core.rxjava.transformer;

import cn.netdiscovery.core.domain.Request;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;

import java.util.concurrent.TimeUnit;

/**
 * Created by tony on 2019-04-26.
 */
public class DownloaderDelayTransformer implements MaybeTransformer {

    private Request request;

    public DownloaderDelayTransformer(Request request) {
        this.request = request;
    }

    @Override
    public MaybeSource apply(Maybe upstream) {

        return request.getDownloadDelay() > 0 ? upstream.delay(request.getDownloadDelay(), TimeUnit.MILLISECONDS) : upstream;
    }
}
