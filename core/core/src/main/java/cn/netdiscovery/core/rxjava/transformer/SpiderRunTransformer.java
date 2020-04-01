package cn.netdiscovery.core.rxjava.transformer;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Executor;

/**
 * @FileName: cn.netdiscovery.core.rxjava.transformer.SpiderRunTransformer
 * @author: Tony Shen
 * @date: 2020-04-02 01:44
 * @version: V1.0 <描述当前版本功能>
 */
public class SpiderRunTransformer implements MaybeTransformer {

    private Executor executor;

    public SpiderRunTransformer(Executor executor) {
        this.executor = executor;
    }

    @Override
    public MaybeSource apply(Maybe upstream) {

        return executor != null ? upstream.observeOn(Schedulers.from(executor)) : upstream;
    }
}
