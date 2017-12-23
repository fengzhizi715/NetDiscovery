package com.cv4j.netdiscovery.core.http;

import com.cv4j.netdiscovery.core.domain.Page;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tony on 2017/12/23.
 */
public class VertxClient {

    private WebClient webClient;
    private Vertx vertx;
    private Request request;
    private URL url;

    public VertxClient(Request request) {

        vertx = Vertx.vertx();
        this.request = request;

        WebClientOptions options = new WebClientOptions();

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

        webClient = WebClient.create(vertx, options);
    }

    public void get() {

        if ("http".equals(url.getProtocol())) {

            webClient.get(url.getHost(),url.getPath())
                    .send(ar->{

                        if (ar.succeeded()) {

                            HttpResponse<Buffer> response = ar.result();

                            String html = response.bodyAsString();

                            Page page = new Page();
                            page.setHtml(html);
                            page.setRequest(request);
                            page.setUrl(request.getUrl());
                            page.setStatusCode(response.statusCode());
                        } else {

                            System.out.println("Something went wrong " + ar.cause().getMessage());
                        }
                    });
        } else if ("https".equals(url.getProtocol())){

            webClient.get(443, url.getHost(), url.getPath())
                    .ssl(true)
                    .send(ar->{

                        if (ar.succeeded()) {

                            HttpResponse<Buffer> response = ar.result();

                            String html = response.bodyAsString();

                            Page page = new Page();
                            page.setHtml(html);
                            page.setRequest(request);
                            page.setUrl(request.getUrl());
                            page.setStatusCode(response.statusCode());
                        } else {

                            System.out.println("Something went wrong " + ar.cause().getMessage());
                        }
                    });
        }


    }


}
