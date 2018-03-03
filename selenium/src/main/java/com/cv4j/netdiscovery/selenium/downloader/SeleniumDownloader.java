package com.cv4j.netdiscovery.selenium.downloader;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.selenium.SeleniumAction;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/1/28.
 */
public class SeleniumDownloader implements Downloader {

    private WebDriver webDriver;
    private SeleniumAction action = null;

    public SeleniumDownloader(WebDriver webDriver) {

        this(webDriver,null);
    }

    public SeleniumDownloader(WebDriver webDriver,SeleniumAction action) {

        this.webDriver = webDriver;
        this.action = action;
    }

    @Override
    public Maybe<Response> download(Request request) {

        return Maybe.create(new MaybeOnSubscribe<String>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                emitter.onSuccess(webDriver.getPageSource());
            }
        }).map(new Function<String, Response>() {

            @Override
            public Response apply(String html) throws Exception {

                Response response = new Response();
                response.setContent(html.getBytes());
                response.setStatusCode(200);
                return response;
            }
        });
    }

    @Override
    public void close() {

        if (webDriver!=null) {
            webDriver.close();
        }
    }
}
