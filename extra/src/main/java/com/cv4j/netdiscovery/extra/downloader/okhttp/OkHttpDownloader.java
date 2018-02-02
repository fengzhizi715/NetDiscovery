package com.cv4j.netdiscovery.extra.downloader.okhttp;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.core.utils.UserAgent;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Map;

/**
 * Created by tony on 2018/1/21.
 */
public class OkHttpDownloader implements Downloader{

    OkHttpClient client;

    public OkHttpDownloader() {
        client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    }

    @Override
    public Maybe<Response> download(Request request) {

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(request.getUrl());

        if (request.getHeader()!=null) {

            for (Map.Entry<String, String> entry:request.getHeader().entrySet()) {
                requestBuilder.addHeader(entry.getKey(),entry.getValue());
            }
        }

        if (Preconditions.isBlank(request.getUserAgent())){

            String ua = UserAgent.getUserAgent();
            if (Preconditions.isNotBlank(ua)) {

                requestBuilder.addHeader("User-Agent",ua);
            }
        }

        okhttp3.Request okrequest = requestBuilder.build();

        return Maybe.create(new MaybeOnSubscribe<okhttp3.Response>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                emitter.onSuccess(client.newCall(okrequest).execute());
            }
        }).map(new Function<okhttp3.Response, Response>() {

            @Override
            public Response apply(okhttp3.Response resp) throws Exception {

                String html = resp.body().string();
                Response response = new Response();
                response.setContent(html);
                response.setStatusCode(resp.code());
                return response;
            }
        });
    }

    @Override
    public void close() {

        try {
            client.dispatcher().executorService().shutdown();   //清除并关闭线程池
            client.connectionPool().evictAll();                 //清除并关闭连接池
            client.cache().close();                             //清除cache
        } catch (IOException e) {

        }

    }
}
