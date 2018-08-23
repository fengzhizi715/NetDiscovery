package com.cv4j.netdiscovery.extra.queue.kafka;

import java.util.Properties;

/**
 * Created by tony on 2018/8/23.
 */
public class KafkaQueueConfig {

    private Properties producerProperties;
    private Properties consumeProperties;
    private String topicName;
    private int partition;

    private KafkaQueueConfig(KafkaQueueConfigBuilder builder) {
        this.producerProperties = builder.producerProperties;
        this.consumeProperties = builder.consumeProperties;
        this.topicName = builder.topicName;
        this.partition = builder.partition;
    }

    public Properties getProducerProperties() {
        return producerProperties;
    }

    public Properties getConsumeProperties() {
        return consumeProperties;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getPartition() {
        return partition;
    }

    public static class KafkaQueueConfigBuilder {

        private Properties producerProperties;
        private Properties consumeProperties;
        private String topicName;
        private int partition;

        public KafkaQueueConfigBuilder(Properties producerProperties, Properties consumeProperties) {
            this.producerProperties = producerProperties;
            this.consumeProperties = consumeProperties;
        }

        public KafkaQueueConfigBuilder topicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public KafkaQueueConfigBuilder partition(int partition) {
            this.partition = partition;
            return this;
        }

        public KafkaQueueConfig build() {
            return new KafkaQueueConfig(this);
        }
    }
}
