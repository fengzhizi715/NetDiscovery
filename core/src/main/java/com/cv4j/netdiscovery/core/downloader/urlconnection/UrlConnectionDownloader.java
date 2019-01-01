package com.cv4j.netdiscovery.core.downloader.urlconnection;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.cookies.Pair;
import com.cv4j.netdiscovery.core.cookies.CookieManager;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tony on 2018/3/2.
 */
@Slf4j
public class UrlConnectionDownloader implements Downloader {

    private URL url = null;
    private HttpURLConnection httpUrlConnection = null;
    private Set<Pair> cookieSet;

    public UrlConnectionDownloader() {
        this.cookieSet = new LinkedHashSet<>();
    }

    @Override
    public Maybe<Response> download(Request request) {

        try {
            url = new URL(request.getUrl());

            // 设置Proxy
            if (request.getProxy()!=null) {

                httpUrlConnection = (HttpURLConnection) url.openConnection(request.getProxy().toJavaNetProxy());
            } else {

                httpUrlConnection = (HttpURLConnection) url.openConnection();
            }

            // 使用Post请求时，设置Post body
            if (request.getHttpMethod() == HttpMethod.POST) {

                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setUseCaches(false); // post 请求不用缓存

                if (request.getHttpRequestBody()!=null) {

                    httpUrlConnection.setRequestProperty(Constant.CONTENT_TYPE, request.getHttpRequestBody().getContentType());

                    OutputStream os = httpUrlConnection.getOutputStream();
                    os.write(request.getHttpRequestBody().getBody());
                    os.flush();
                    os.close();
                }
            }

            //设置请求头header
            if (Preconditions.isNotBlank(request.getHeader())) {

                for (Map.Entry<String, String> entry:request.getHeader().entrySet()) {
                    httpUrlConnection.setRequestProperty(entry.getKey(),entry.getValue());
                }
            }

            //设置字符集
            if (Preconditions.isNotBlank(request.getCharset())) {

                httpUrlConnection.setRequestProperty("Accept-Charset", request.getCharset());
            }

            httpUrlConnection.connect();

           return Maybe.create(new MaybeOnSubscribe<InputStream>() {

                @Override
                public void subscribe(MaybeEmitter<InputStream> emitter) throws Exception {

                    emitter.onSuccess(httpUrlConnection.getInputStream());
                }
            }).map(new Function<InputStream, Response>() {

                @Override
                public Response apply(InputStream inputStream) throws Exception {

                    Response response = new Response();
                    response.setContent(IOUtils.readInputStream(inputStream));
                    response.setStatusCode(httpUrlConnection.getResponseCode());
                    response.setContentType(httpUrlConnection.getContentType());

                    if (request.isSaveCookie()) {

                        // save cookies
                        CookieManager.getInsatance().saveCookie(request,cookieSet,httpUrlConnection.getHeaderField(Constant.SET_COOKIES_HEADER));
                    }

                    return response;
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Maybe.empty();
    }

    @Override
    public void close() throws IOException {

        if (httpUrlConnection!=null) {

            httpUrlConnection.disconnect();
        }
    }
}
