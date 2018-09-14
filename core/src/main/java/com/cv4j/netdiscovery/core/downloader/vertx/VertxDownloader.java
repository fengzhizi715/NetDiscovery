package com.cv4j.netdiscovery.core.downloader.vertx;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.cookies.Cookie;
import com.cv4j.netdiscovery.core.cookies.CookieManager;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.core.utils.VertxUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tony on 2017/12/23.
 */
public class VertxDownloader implements Downloader {

    private WebClient webClient;
    private io.vertx.reactivex.core.Vertx vertx;
    private Map<String, String> header;
    private Set<Cookie> cookieSet;

    public VertxDownloader() {

        this.vertx = VertxUtils.reactivex_vertx;
        this.cookieSet = new LinkedHashSet<>();
    }

    public Maybe<Response> download(Request request) {

        WebClientOptions options = initWebClientOptions(request);

        webClient = WebClient.create(vertx, options);

        HttpRequest<Buffer> httpRequest = null;

        if (request.getHttpMethod() == HttpMethod.GET) {

            if ("http".equals(request.getUrlParser().getProtocol())) {

                httpRequest = webClient.getAbs(request.getUrl());

            } else if ("https".equals(request.getUrlParser().getProtocol())) {

                httpRequest = webClient.get(443, request.getUrlParser().getHost(), Preconditions.isNotBlank(request.getUrlParser().getQuery()) ? request.getUrlParser().getPath() + "?" + request.getUrlParser().getQuery() : request.getUrlParser().getPath()).ssl(true);
            }
        } else if (request.getHttpMethod() == HttpMethod.POST) {

            if ("http".equals(request.getUrlParser().getProtocol())) {

                httpRequest = webClient.postAbs(request.getUrl());

            } else if ("https".equals(request.getUrlParser().getProtocol())) {

                httpRequest = webClient.post(443, request.getUrlParser().getHost(), Preconditions.isNotBlank(request.getUrlParser().getQuery()) ? request.getUrlParser().getPath() + "?" + request.getUrlParser().getQuery() : request.getUrlParser().getPath()).ssl(true);
            }
        }

        //设置请求头header
        if (Preconditions.isNotBlank(header)) {

            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpRequest.putHeader(entry.getKey(), entry.getValue());
            }
        }

        // 针对post请求，需要对header添加一些信息
        if (request.getHttpMethod() == HttpMethod.POST) {

            if (Preconditions.isNotBlank(request.getHttpRequestBody()) && Preconditions.isNotBlank(request.getHttpRequestBody().getContentType())) {

                httpRequest.putHeader(Constant.CONTENT_TYPE, request.getHttpRequestBody().getContentType());
            }
        }

        String charset = null;
        if (Preconditions.isNotBlank(request.getCharset())) {
            charset = request.getCharset();
        } else {
            charset = Constant.UTF_8;
        }

        HttpRequest<String> stringHttpRequest = httpRequest.as(BodyCodec.string(charset));
        Single<HttpResponse<String>> httpResponseSingle = null;

        if (request.getHttpMethod() == HttpMethod.GET) {

            httpResponseSingle = stringHttpRequest.rxSend();
        } else if (request.getHttpMethod() == HttpMethod.POST) {

            if (Preconditions.isNotBlank(request.getHttpRequestBody())) {

                Buffer buffer = Buffer.buffer();
                buffer.getDelegate().appendBytes(request.getHttpRequestBody().getBody());
                httpResponseSingle = stringHttpRequest.rxSendBuffer(buffer);
            } else {

                httpResponseSingle = stringHttpRequest.rxSend();
            }
        }

        return httpResponseSingle
                .toMaybe()
                .map(new Function<HttpResponse<String>, Response>() {
                    @Override
                    public Response apply(HttpResponse<String> stringHttpResponse) throws Exception {

                        String html = stringHttpResponse.body();
                        Response response = new Response();
                        response.setContent(html.getBytes());
                        response.setStatusCode(stringHttpResponse.statusCode());
                        response.setContentType(stringHttpResponse.getHeader(Constant.CONTENT_TYPE));

                        if (request.isSaveCookie()) {

                            // save cookies
                            CookieManager.getInsatance().saveCookie(request, cookieSet, stringHttpResponse.cookies());
                        }

                        return response;
                    }
                });
    }

    private WebClientOptions initWebClientOptions(Request request) {

        WebClientOptions options = new WebClientOptions();
        options.setKeepAlive(true).setReuseAddress(true).setFollowRedirects(true).setConnectTimeout(10000)
                .setIdleTimeout(10).setMaxWaitQueueSize(10);

        if (Preconditions.isNotBlank(request.getUserAgent())) {
            options.setUserAgent(request.getUserAgent());
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

        if (webClient != null) {
            webClient.close();
        }
    }
}
