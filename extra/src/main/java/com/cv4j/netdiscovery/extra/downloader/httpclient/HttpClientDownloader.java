package com.cv4j.netdiscovery.extra.downloader.httpclient;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.cookies.CookiePool;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by tony on 2018/1/20.
 */
@Slf4j
public class HttpClientDownloader implements Downloader{

    HttpManager httpManager;

    public HttpClientDownloader() {

        httpManager = HttpManager.get();
    }

    @Override
    public Maybe<Response> download(final Request request) {

        return Maybe.create(new MaybeOnSubscribe<CloseableHttpResponse>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                emitter.onSuccess(httpManager.getResponse(request));
            }
        }).map(new Function<CloseableHttpResponse, Response>() {

            @Override
            public Response apply(CloseableHttpResponse closeableHttpResponse) throws Exception {

                String charset = null;
                if (Preconditions.isNotBlank(request.getCharset())) {
                    charset = request.getCharset();
                } else {
                    charset = Constant.UTF_8;
                }

                String html = EntityUtils.toString(closeableHttpResponse.getEntity(), charset);
                Response response = new Response();
                response.setContent(html.getBytes());
                response.setStatusCode(closeableHttpResponse.getStatusLine().getStatusCode());
                if (closeableHttpResponse.containsHeader(Constant.CONTENT_TYPE)) {
                    response.setContentType(closeableHttpResponse.getFirstHeader(Constant.CONTENT_TYPE).getValue());
                }

                if (request.isSaveCookie()) {

                    // save cookies
                    Header[] headers = closeableHttpResponse.getHeaders(Constant.SET_COOKIES_HEADER);

                    if (Preconditions.isNotBlank(headers)) {

                        for (Header header:headers) {

                            CookiePool.getInsatance().saveCookie(request,header.getValue());
                        }
                    }
                }

                return response;
            }
        });
    }

    @Override
    public void close() {

//        httpManager.close();
    }
}
