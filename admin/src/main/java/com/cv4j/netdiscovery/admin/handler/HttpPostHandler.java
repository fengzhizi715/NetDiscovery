package com.cv4j.netdiscovery.admin.handler;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;

import java.nio.Buffer;

public class HttpPostHandler implements Handler {

    private RoutingContext routingContext;

    public HttpPostHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public void handle(Object event) {
        AsyncResult<HttpResponse<Buffer>> response = (AsyncResult<HttpResponse<Buffer>>)event;

        if(response.succeeded()) {
            CommonUtil.sendJsonToResponse(routingContext, response.result().bodyAsJsonObject());
        } else {
            response.cause().printStackTrace();
            CommonUtil.sendJsonToResponse(routingContext, "handle http post failure");
        }
    }
}
