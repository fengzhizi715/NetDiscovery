package com.cv4j.netdiscovery.core.utils;

import io.vertx.core.Vertx;

/**
 * Created by tony on 2017/12/28.
 */
public class VertxUtils {

    private static Vertx vertx;
    private static io.vertx.reactivex.core.Vertx reactivex_vertx;

    static{

        vertx = Vertx.vertx();
        reactivex_vertx = new io.vertx.reactivex.core.Vertx(vertx);
    }

    public static Vertx getVertx() {
        return vertx;
    }

    public static io.vertx.reactivex.core.Vertx getReactivexVertx() {
        return reactivex_vertx;
    }
}
