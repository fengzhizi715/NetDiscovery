package cn.netdiscovery.core.utils;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * Created by tony on 2017/12/28.
 */
public class VertxUtils {

    private static Vertx vertx;
    private static io.vertx.reactivex.core.Vertx reactivex_vertx;

    static{
        VertxOptions options = new VertxOptions();
        options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        vertx = Vertx.vertx(options);
        reactivex_vertx = new io.vertx.reactivex.core.Vertx(vertx);
    }

    public static Vertx getVertx() {
        return vertx;
    }

    public static io.vertx.reactivex.core.Vertx getReactivexVertx() {
        return reactivex_vertx;
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
}
