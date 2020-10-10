package cn.netdiscovery.example.rabbit;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.queue.rabbitmq.RabbitQueue;
import cn.netdiscovery.queue.rabbitmq.RabbitQueueConfig;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author bdq
 * @date 2018-12-28
 */
public class TestRabbit {
    public static void main(String[] args) {

        Properties producerProperties = new Properties();
        producerProperties.put("rabbitmq.username", "guest");
        producerProperties.put("rabbitmq.password", "guest");
        producerProperties.put("rabbitmq.virtual.host", "/");
        producerProperties.put("rabbitmq.host", "localhost");
        producerProperties.put("rabbitmq.port", "5672");

        Properties consumeProperties = new Properties();
        consumeProperties.put("rabbitmq.username", "guest");
        consumeProperties.put("rabbitmq.password", "guest");
        consumeProperties.put("rabbitmq.virtual.host", "/");
        consumeProperties.put("rabbitmq.host", "localhost");
        consumeProperties.put("rabbitmq.port", "5672");

        RabbitQueueConfig config = new RabbitQueueConfig.RabbitQueueConfigBuilder(producerProperties, consumeProperties)
                .producerExchange("tony")
                .consumerExchange("tony")
                .queueNames(Arrays.asList("bdq"))
                .build();

        RabbitQueue queue = new RabbitQueue(config);

        Request request = new Request("https://www.baidu.com").checkDuplicate(false);

        Spider.create(queue)
                .name("bdq")
                .request(request)
                .run();
    }
}
