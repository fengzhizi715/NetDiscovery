package com.cv4j.netdiscovery.admin.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        Router router = Router.router(vertx);
        router.route("/admin/*").handler(StaticHandler.create("templates"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8016);

        log.info("NetDiscovery admin listening on 8016");
    }
}
