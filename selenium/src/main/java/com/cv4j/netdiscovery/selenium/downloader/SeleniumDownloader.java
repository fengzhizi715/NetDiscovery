package com.cv4j.netdiscovery.selenium.downloader;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.selenium.action.SeleniumAction;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import org.openqa.selenium.JavascriptExecutor;
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

                webDriver.get(request.getUrl());

                if (action != null) {
                    action.perform(webDriver);
                }

                String content = webDriver.getPageSource();

                emitter.onSuccess(content);
            }
        }).map(new Function<String, Response>() {

            @Override
            public Response apply(String html) throws Exception {

                Response response = new Response();
                response.setContent(html.getBytes());
                response.setStatusCode(Constant.OK_STATUS_CODE);
                response.setContentType(getContentType(webDriver));
                return response;
            }
        });
    }

    /**
     * @param wd
     * @return
     */
    private String getContentType(final WebDriver wd) {
        if (wd instanceof JavascriptExecutor) {
            final JavascriptExecutor jsExecutor = (JavascriptExecutor) wd;
            // TODO document.contentType does not exist.
            final Object ret = jsExecutor
                    .executeScript("return document.contentType;");
            if (ret != null) {
                return ret.toString();
            }
        }
        return "text/html";
    }


    @Override
    public void close() {

        if (webDriver!=null) {
            webDriver.quit();
        }
    }
}
