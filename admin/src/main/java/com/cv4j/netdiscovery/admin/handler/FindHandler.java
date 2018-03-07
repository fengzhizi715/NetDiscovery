package com.cv4j.netdiscovery.admin.handler;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FindHandler implements Handler {

    private RoutingContext routingContext;

    public FindHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public void handle(Object event) {
        AsyncResult<List<JsonObject>> asyncResult = (AsyncResult<List<JsonObject>>)event;

        if(asyncResult.succeeded()) {
            List<JsonObject> resultList = asyncResult.result();
            CommonUtil.sendJsonToResponse(routingContext, resultList);
        } else {
            asyncResult.cause().printStackTrace();
            CommonUtil.sendJsonToResponse(routingContext, "handle find failure");
        }
    }

}
