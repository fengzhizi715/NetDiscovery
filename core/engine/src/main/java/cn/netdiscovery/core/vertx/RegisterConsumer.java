package cn.netdiscovery.core.vertx;

/**
 * 注册 Vert.x eventBus 的消费者
 * @FileName: cn.netdiscovery.core.vertx.RegisterConsumer
 * @author: Tony Shen
 * @date: 2020-03-17 13:20
 * @version: V1.0 <描述当前版本功能>
 */
@FunctionalInterface
public interface RegisterConsumer {
    void process();
}