package cn.netdiscovery.core.downloader.vertx;

import cn.netdiscovery.core.cache.RxCacheManager;
import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.config.SpiderConfig;
import cn.netdiscovery.core.cookies.CookiesPool;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.rxjava.transformer.DownloaderDelayTransformer;
import cn.netdiscovery.core.vertx.VertxManager;
import com.safframework.rxcache.domain.Record;
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

import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
public class VertxDownloader implements Downloader {

    private WebClient webClient;
    private io.vertx.reactivex.core.Vertx vertx;
    private Map<String, String> header;

    public VertxDownloader() {

        this.vertx = new io.vertx.reactivex.core.Vertx(VertxManager.getVertx());
    }

    public Maybe<Response> download(Request request) {

        // request 在 debug 模式下，并且缓存中包含了数据，则使用缓存中的数据
        if (request.getDebug()
                && RxCacheManager.getInstance().getRxCache()!=null
                && RxCacheManager.getInstance().getRxCache().get(request.getUrl(), Response.class)!=null) {

            Record<Response> response = RxCacheManager.getInstance().getRxCache().get(request.getUrl(), Response.class);
            return Maybe.just(response.getData());
        }

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
        if (request.getHttpMethod() == HttpMethod.POST
                && Preconditions.isNotBlank(request.getHttpRequestBody())
                && Preconditions.isNotBlank(request.getHttpRequestBody().getContentType())) {

            httpRequest.putHeader(Constant.CONTENT_TYPE, request.getHttpRequestBody().getContentType());
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
                .compose(new DownloaderDelayTransformer(request))
                .map(new Function<HttpResponse<String>, Response>() {
                    @Override
                    public Response apply(HttpResponse<String> stringHttpResponse) throws Exception {

                        String html = stringHttpResponse.body();
                        Response response = new Response();
                        response.setContent(html.getBytes());
                        response.setStatusCode(stringHttpResponse.statusCode());
                        response.setContentType(stringHttpResponse.getHeader(Constant.CONTENT_TYPE));

                        if (request.getSaveCookie()) {
                            // save cookies
                            CookiesPool.getInsatance().saveCookie(request, stringHttpResponse.cookies());
                        }

                        if (request.getDebug()) { // request 在 debug 模式，则缓存response

                            save(request.getUrl(),response);
                        }

                        return response;
                    }
                });
    }

    private WebClientOptions initWebClientOptions(Request request) {

        WebClientOptions options = new WebClientOptions();
        options.setKeepAlive(SpiderConfig.INSTANCE.getKeepAlive())
                .setReuseAddress(SpiderConfig.INSTANCE.getReuseAddress())
                .setFollowRedirects(SpiderConfig.INSTANCE.getFollowRedirects())
                .setConnectTimeout(SpiderConfig.INSTANCE.getConnectTimeout())
                .setIdleTimeout(SpiderConfig.INSTANCE.getIdleTimeout())
                .setMaxWaitQueueSize(SpiderConfig.INSTANCE.getMaxWaitQueueSize());

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

    @Override
    public void close() {

        if (webClient != null) {
            webClient.close();
        }
    }
}
