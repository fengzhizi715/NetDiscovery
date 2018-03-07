package com.cv4j.netdiscovery.admin;

import com.cv4j.netdiscovery.admin.dao.MongoDao;
import com.cv4j.netdiscovery.admin.verticle.*;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LaunchAdmin {

    public final static Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        MongoDao.getInstance().init();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/admin/*").handler(StaticHandler.create("templates"));

        vertx.deployVerticle(new ResourceVerticle(router));  //管理资源
        vertx.deployVerticle(new JobVerticle(router));       //管理job
        vertx.deployVerticle(new ProxyVerticle(router));     //管理job获取的ip
        vertx.deployVerticle(new SpiderVerticle(router));    //管理爬虫引擎

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8016);

        log.info("NetDiscovery admin listening on 8016");
    }
}
