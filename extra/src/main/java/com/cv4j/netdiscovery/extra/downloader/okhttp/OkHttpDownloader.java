package com.cv4j.netdiscovery.extra.downloader.okhttp;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;

import java.util.Map;

/**
 * Created by tony on 2018/1/21.
 */
public class OkHttpDownloader implements Downloader{

    OkHttpClient client;

    public OkHttpDownloader() {
        client = new OkHttpClient();
    }

    @Override
    public Maybe<Response> download(Request request) {

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(request.getUrl());

        if (request.getHeader()!=null) {

            for (Map.Entry<String, String> entry:request.getHeader().entrySet()) {
                requestBuilder.addHeader(entry.getKey(),entry.getValue());
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
    }
}
