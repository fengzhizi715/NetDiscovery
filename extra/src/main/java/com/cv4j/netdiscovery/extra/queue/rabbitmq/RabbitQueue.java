package com.cv4j.netdiscovery.extra.queue.rabbitmq;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.cv4j.netdiscovery.core.utils.SerializableUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author bdq
 * @date 2018-12-28
 */
public class RabbitQueue extends AbstractQueue {
    private String producerExchange;
    private String consumerExchange;
    private Channel producer;
    private Channel consumer;
    private List<String> queueNames;

    public RabbitQueue(RabbitQueueConfig rabbitQueueConfig) {
        producerExchange = rabbitQueueConfig.getProducerExchange();
        consumerExchange = rabbitQueueConfig.getConsumerExchange();
        queueNames = rabbitQueueConfig.getQueueNames();
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
            producer.exchangeDeclare(producerExchange, "topic");
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
            consumer.exchangeDeclare(consumerExchange, "topic");
            for (String queueName : queueNames) {
                consumer.queueDeclare(queueName, false, false, false, null);
                consumer.queueBind(queueName, consumerExchange, queueName);
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void pushWhenNoDuplicate(Request request) {
        try {
            producer.basicPublish(producerExchange, request.getSpiderName(), null, serialize(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(Request request) {
        return SerializableUtils.toJson(request).getBytes();
    }

    @Override
    public Request poll(String spiderName) {
        Request request = null;
        try {
            GetResponse response = consumer.basicGet(spiderName, true);
            request = deserialize(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    private Request deserialize(byte[] data) {
        return SerializableUtils.fromJson(new String(data), Request.class);
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
