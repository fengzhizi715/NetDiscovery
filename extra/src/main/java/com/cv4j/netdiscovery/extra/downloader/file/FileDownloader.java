package com.cv4j.netdiscovery.extra.downloader.file;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.extra.downloader.httpclient.HttpManager;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import io.vertx.core.http.HttpMethod;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Created by tony on 2018/3/11.
 */
public class FileDownloader implements Downloader{

    HttpManager httpManager;

    public FileDownloader() {

        httpManager = HttpManager.get();
    }

    @Override
    public Maybe<Response> download(final Request request) {

        return Maybe.create(new MaybeOnSubscribe<CloseableHttpResponse>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                request.httpMethod(HttpMethod.POST);

                if (request.getHeader().get("Referer")==null) {

                    request.referer(request.getUrlParser().getHost());
                }

                emitter.onSuccess(httpManager.getResponse(request));
            }
        }).map(new Function<CloseableHttpResponse, Response>() {

            @Override
            public Response apply(CloseableHttpResponse closeableHttpResponse) throws Exception {

                Response response = new Response();
                response.setIs(closeableHttpResponse.getEntity().getContent());
                response.setStatusCode(closeableHttpResponse.getStatusLine().getStatusCode());
                if (closeableHttpResponse.containsHeader("Content-Type")) {
                    response.setContentType(closeableHttpResponse.getFirstHeader("Content-Type").getValue());
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
