package com.cv4j.netdiscovery.admin.handler;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateHandler implements Handler {

    private RoutingContext routingContext;

    public UpdateHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public void handle(Object event) {
//        AsyncResult<MongoClientUpdateResult> asyncResult = (AsyncResult<MongoClientUpdateResult>)event;
        AsyncResult<JsonObject> asyncResult = (AsyncResult<JsonObject>)event;

        if(asyncResult.succeeded()) {
            CommonUtil.sendJsonToResponse(routingContext, asyncResult.result());
        } else {
            asyncResult.cause().printStackTrace();
            CommonUtil.sendJsonToResponse(routingContext, "handle update failure");
        }
    }

}
