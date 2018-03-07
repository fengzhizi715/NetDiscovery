package com.cv4j.netdiscovery.admin.handler;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertHandler implements Handler {

    private RoutingContext routingContext;

    public InsertHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public void handle(Object event) {
        AsyncResult<String> asyncResult = (AsyncResult<String>)event;

        if(asyncResult.succeeded()) {
            CommonUtil.sendJsonToResponse(routingContext, asyncResult.result());
        } else {
            asyncResult.cause().printStackTrace();
            CommonUtil.sendJsonToResponse(routingContext, "handle save failure");
        }
    }

}
