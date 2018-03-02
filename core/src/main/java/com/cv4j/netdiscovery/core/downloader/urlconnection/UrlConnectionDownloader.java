package com.cv4j.netdiscovery.core.downloader.urlconnection;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.safframework.tony.common.utils.IOUtils;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tony on 2018/3/2.
 */
@Slf4j
public class UrlConnectionDownloader implements Downloader {

    private URL url = null;
    private HttpURLConnection httpUrlConnection = null;

    public UrlConnectionDownloader() {
    }

    @Override
    public Maybe<Response> download(Request request) {

        try {
            url = new URL(request.getUrl());
            httpUrlConnection = (HttpURLConnection) url.openConnection();
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
                    response.setContentType(httpUrlConnection.getHeaderField("Content-Type"));
                    return response;
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void close() throws IOException {

//        if (httpUrlConnection!=null) {
//
//            httpUrlConnection.disconnect();
//        }
    }
}
