package com.cv4j.netdiscovery.core.http;

import com.cv4j.netdiscovery.core.utils.VertxUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.functions.Function;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
public class VertxClient {

    private WebClient webClient;
    private io.vertx.reactivex.core.Vertx vertx;
    private URL url;
    private Map<String,String> header;

    public VertxClient(Request request) {

        this.vertx = VertxUtils.vertx;

        WebClientOptions options = new WebClientOptions();
        options.setKeepAlive(true).setReuseAddress(true);

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

        if (Preconditions.isNotBlank(request.getProxy())) {

            ProxyOptions proxyOptions = new ProxyOptions();
            proxyOptions.setHost(request.getProxy().getIp());
            proxyOptions.setPort(request.getProxy().getPort());
            options.setProxyOptions(proxyOptions);
        }

        if (Preconditions.isNotBlank(request.getHeader())) {

            header = request.getHeader();
        }

        webClient = WebClient.create(vertx, options);
    }

    public Maybe<Response> request() {

        Maybe<Response> result = null;
        HttpRequest<Buffer> request = null;

        if ("http".equals(url.getProtocol())) {

            request = webClient.get(url.getHost(),url.getPath());

            if (Preconditions.isNotBlank(header)) {

                for (Map.Entry<String, String> entry:header.entrySet()) {
                    request.putHeader(entry.getKey(),entry.getValue());
                }
            }

        } else if ("https".equals(url.getProtocol())){

            request = webClient.get(443, url.getHost(), url.getPath())
                    .ssl(true);

            if (Preconditions.isNotBlank(header)) {

                for (Map.Entry<String, String> entry:header.entrySet()) {
                    request.putHeader(entry.getKey(),entry.getValue());
                }
            }
        }

        result = request
                .as(BodyCodec.string())
                .rxSend()
                .toMaybe()
                .map(new Function<HttpResponse<String>, Response>() {
                    @Override
                    public Response apply(HttpResponse<String> stringHttpResponse) throws Exception {

                        String html = stringHttpResponse.body();
                        Response response = new Response();
                        response.content = html;
                        response.statusCode = stringHttpResponse.statusCode();

                        return response;
                    }
                });

        return result;
    }

    public void close() {

        webClient.close();
    }
}
