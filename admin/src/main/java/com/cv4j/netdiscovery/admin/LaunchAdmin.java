package com.cv4j.netdiscovery.admin;

import com.cv4j.netdiscovery.admin.verticle.AdminVerticle;
import io.vertx.core.Vertx;

public class LaunchAdmin {

    public final static Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        vertx.deployVerticle(new AdminVerticle());
    }
}
