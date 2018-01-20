package com.cv4j.netdiscovery.core.http;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.download.Downloader;
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
public class VertxClient implements Downloader {

    private WebClient webClient;
    private io.vertx.reactivex.core.Vertx vertx;
    private URL url;
    private Map<String,String> header;

    public VertxClient() {

        this.vertx = VertxUtils.vertx;
    }

    public Maybe<Response> download(Request request) {

        WebClientOptions options = initWebClientOptions(request);

        webClient = WebClient.create(vertx, options);

        HttpRequest<Buffer> httpRequest = null;

        if ("http".equals(url.getProtocol())) {

            httpRequest = webClient.get(url.getHost(),url.getPath());

            if (Preconditions.isNotBlank(header)) {

                for (Map.Entry<String, String> entry:header.entrySet()) {
                    httpRequest.putHeader(entry.getKey(),entry.getValue());
                }
            }

        } else if ("https".equals(url.getProtocol())){

            httpRequest = webClient.get(443, url.getHost(), url.getPath())
                    .ssl(true);

            if (Preconditions.isNotBlank(header)) {

                for (Map.Entry<String, String> entry:header.entrySet()) {
                    httpRequest.putHeader(entry.getKey(),entry.getValue());
                }
            }
        }

        return httpRequest
                .as(BodyCodec.string())
                .rxSend()
                .toMaybe()
                .map(new Function<HttpResponse<String>, Response>() {
                    @Override
                    public Response apply(HttpResponse<String> stringHttpResponse) throws Exception {

                        String html = stringHttpResponse.body();
                        Response response = new Response();
                        response.setContent(html);
                        response.setStatusCode(stringHttpResponse.statusCode());

                        return response;
                    }
                });
    }

    private WebClientOptions initWebClientOptions(Request request) {

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

        return options;
    }

    public void close() {

        webClient.close();
    }
}
