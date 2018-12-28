package com.cv4j.netdiscovery.extra.queue.rabbitmq;

import java.util.Properties;

/**
 * @author bdq
 * @date 2018-12-28
 */
public class RabbitQueueConfig {
    private Properties producerProperties;
    private Properties consumeProperties;
    private String exchange;
    /**
     * 必须与spiderName一致
     */
    private String queueName;

    public RabbitQueueConfig(RabbitQueueConfigBuilder builder) {
        producerProperties = builder.producerProperties;
        consumeProperties = builder.consumeProperties;
        exchange = builder.exchange;
        queueName = builder.queueName;
    }

    public Properties getProducerProperties() {
        return producerProperties;
    }

    public Properties getConsumeProperties() {
        return consumeProperties;
    }

    public String getExchange() {
        return exchange;
    }

    public String getQueueName() {
        return queueName;
    }

    public static class RabbitQueueConfigBuilder {
        private Properties producerProperties;
        private Properties consumeProperties;
        private String exchange;
        private String queueName;

        public RabbitQueueConfigBuilder(Properties producerProperties, Properties consumeProperties) {
            this.producerProperties = producerProperties;
            this.consumeProperties = consumeProperties;
        }

        public RabbitQueueConfigBuilder exchange(String exchange) {
            this.exchange = exchange;
            return this;
        }

        public RabbitQueueConfigBuilder queueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public RabbitQueueConfig build() {
            return new RabbitQueueConfig(this);
        }
    }
}
