package com.cv4j.netdiscovery.core.http;

import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

/**
 * Created by tony on 2017/12/23.
 */
public class VertxClient {

    private WebClient webClient;
    private Vertx vertx;
    private String url;

    public VertxClient(Request request) {

        vertx = Vertx.vertx();

        WebClientOptions options = new WebClientOptions();

        if (Preconditions.isNotBlank(request.getUserAgent())) {
            options.setUserAgent(request.getUserAgent());
        }

        if (Preconditions.isNotBlank(request.getUrl())) {
            url = request.getUrl();
        }

        webClient = WebClient.create(vertx, options);
    }

    public void get() {

        webClient.get(url)
                .send(ar->{

                    if (ar.succeeded()) {

                    }
                });
    }


}
