package com.cv4j.netdiscovery.extra.queue.rabbitmq;

import lombok.Getter;

import java.util.List;
import java.util.Properties;

/**
 * @author bdq
 * @date 2018-12-28
 */
@Getter
public class RabbitQueueConfig {
    private Properties producerProperties;
    private Properties consumeProperties;
    private String producerExchange;
    private String consumerExchange;
    private List<String> queueNames;

    public RabbitQueueConfig(RabbitQueueConfigBuilder builder) {
        producerProperties = builder.producerProperties;
        consumeProperties = builder.consumeProperties;
        producerExchange = builder.producerExchange;
        consumerExchange=builder.consumerExchange;
        queueNames = builder.queueNames;
    }

    public static class RabbitQueueConfigBuilder {
        private Properties producerProperties;
        private Properties consumeProperties;
        private String producerExchange;
        private String consumerExchange;
        private List<String> queueNames;

        public RabbitQueueConfigBuilder(Properties producerProperties, Properties consumeProperties) {
            this.producerProperties = producerProperties;
            this.consumeProperties = consumeProperties;
        }

        public RabbitQueueConfigBuilder producerExchange(String producerExchange) {
            this.producerExchange = producerExchange;
            return this;
        }

        public RabbitQueueConfigBuilder consumerExchange(String consumerExchange) {
            this.consumerExchange = consumerExchange;
            return this;
        }

        public RabbitQueueConfigBuilder queueNames(List<String> queueNames) {
            this.queueNames = queueNames;
            return this;
        }

        public RabbitQueueConfig build() {
            return new RabbitQueueConfig(this);
        }
    }
}
