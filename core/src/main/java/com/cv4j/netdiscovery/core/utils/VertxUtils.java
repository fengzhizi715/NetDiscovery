package com.cv4j.netdiscovery.core.utils;

import io.vertx.core.Vertx;

/**
 * Created by tony on 2017/12/28.
 */
public class VertxUtils {

    public static Vertx vertx = Vertx.vertx();

    public static io.vertx.reactivex.core.Vertx reactivex_vertx = new io.vertx.reactivex.core.Vertx(vertx);

    public static Vertx getVertx() {
        return vertx;
    }

    public static void setVertx(Vertx vertx) {
        VertxUtils.vertx = vertx;
    }
}
