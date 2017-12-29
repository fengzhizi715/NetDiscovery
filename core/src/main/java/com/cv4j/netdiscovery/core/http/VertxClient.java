package com.cv4j.netdiscovery.core.http;

import com.cv4j.netdiscovery.core.utils.VertxUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Single;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tony on 2017/12/23.
 */
public class VertxClient {

    private WebClient webClient;
    private io.vertx.reactivex.core.Vertx vertx;
    private Request request;
    private URL url;

    public VertxClient(Request request) {

        vertx = VertxUtils.vertx;
        this.request = request;

        WebClientOptions options = new WebClientOptions();

        if (Preconditions.isNotBlank(request.getUserAgent())) {
            options.setUserAgent(request.getUserAgent());
        }

        if (Preconditions.isNotBlank(request.getUrl())) {
            try {
                url = new URL(request.getUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        webClient = WebClient.create(vertx, options);
    }

    public Single<HttpResponse<String>> get() {

        Single<HttpResponse<String>> result = null;

        if ("http".equals(url.getProtocol())) {

            result = webClient.get(url.getHost(),url.getPath())
                    .as(BodyCodec.string())
                    .rxSend();

        } else if ("https".equals(url.getProtocol())){

            result = webClient.get(443, url.getHost(), url.getPath())
                    .ssl(true)
                    .as(BodyCodec.string())
                    .rxSend();
        }

        return result;
    }

    public void close() {

        webClient.close();
    }
}
