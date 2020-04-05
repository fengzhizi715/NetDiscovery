package cn.netdiscovery.core.rxjava.transformer;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Executor;

/**
 * 爬虫运行时的 Transformer，便于 downloader、parser、pipeline 等过程使用单独的线程池运行
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
