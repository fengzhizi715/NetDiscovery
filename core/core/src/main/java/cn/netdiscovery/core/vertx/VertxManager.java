package cn.netdiscovery.core.vertx;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * Created by tony on 2017/12/28.
 */
public class VertxManager {

    private static Vertx vertx;

    static{
        VertxOptions options = new VertxOptions();
        options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        vertx = Vertx.vertx(options);
    }

    public static Vertx getVertx() {
        return vertx;
    }

    public static void configVertx(VertxOptions options) {
        vertx = Vertx.vertx(options);
    }

    public static EventBus send(String address, Object message) {
        return vertx.eventBus().send(address,message);
    }

    public static EventBus publish(String address, Object message) {
        return vertx.eventBus().publish(address,message);
    }

    public static <T> MessageConsumer<T> consumer(String address, Handler<Message<T>> handler) {
        return vertx.eventBus().consumer(address,handler);
    }

    public static void deployVerticle(Verticle verticle) {
        vertx.deployVerticle(verticle);
    }

    public static void deployVerticle(Verticle verticle, DeploymentOptions options) {
        vertx.deployVerticle(verticle, options);
    }

    public static void undeploy(String deploymentID) {
        vertx.undeploy(deploymentID);
    }
}
