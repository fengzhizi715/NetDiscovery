package com.cv4j.netdiscovery.extra.queue.rabbitmq;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author bdq
 * @date 2018-12-28
 */
public class RabbitQueue extends AbstractQueue {
    private String exchange;
    private String queueName;
    private Channel producer;
    private Channel consumer;

    public RabbitQueue(RabbitQueueConfig rabbitQueueConfig) {
        exchange = rabbitQueueConfig.getExchange();
        queueName = rabbitQueueConfig.getQueueName();
        initProducer(rabbitQueueConfig);
        initConsumer(rabbitQueueConfig);
    }

    private void initProducer(RabbitQueueConfig rabbitQueueConfig) {
        ConnectionFactory factory = new ConnectionFactory();
        ConnectionFactoryConfigurator.load(factory, rabbitQueueConfig.getProducerProperties());
        Connection connection;
        try {
            connection = factory.newConnection();
            producer = connection.createChannel();
            producer.exchangeDeclare(exchange, "direct");
            producer.queueDeclare(queueName, false, false, false, null);
            producer.queueBind(queueName, exchange, "");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void initConsumer(RabbitQueueConfig rabbitQueueConfig) {
        ConnectionFactory factory = new ConnectionFactory();
        ConnectionFactoryConfigurator.load(factory, rabbitQueueConfig.getConsumeProperties());
        Connection connection;
        try {
            connection = factory.newConnection();
            consumer = connection.createChannel();
            consumer.exchangeDeclare(exchange, "direct");
            consumer.queueDeclare(queueName, false, false, false, null);
            consumer.queueBind(queueName, exchange, "");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void pushWhenNoDuplicate(Request request) {
        try {
            producer.basicPublish(exchange, "", null, serialize(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(Request request) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(request).getBytes();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public Request poll(String spiderName) {
        AtomicReference<Request> request = new AtomicReference<>();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                request.set(deserialize(delivery));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        try {
            consumer.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request.get();
    }

    private Request deserialize(Delivery delivery) {
        ObjectMapper mapper = new ObjectMapper();
        Request request = null;
        try {
            request = mapper.readValue(delivery.getBody(), Request.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {
        try {
            return (int) consumer.messageCount(spiderName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
