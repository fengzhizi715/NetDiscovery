package com.cv4j.netdiscovery.extra.downloader.okhttp;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by tony on 2018/2/26.
 */
public class RedirectInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        HttpUrl beforeUrl = request.url();
        Response response = chain.proceed(request);
        HttpUrl afterUrl = response.request().url();
        //根据url判断是否是重定向 处理两种情况 1、跨协议 2、原先不是GET请求。
        if(!beforeUrl.equals(afterUrl)
                && (!beforeUrl.scheme().equals(afterUrl.scheme())||!"GET".equals(request.method()))) {

            Request newRequest = request.newBuilder().url(response.request().url()).build();
            response = chain.proceed(newRequest);
        }
        return response;
    }
}
