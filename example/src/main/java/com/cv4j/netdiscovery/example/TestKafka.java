package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.extra.queue.kafka.KafkaQueue;

import java.util.Properties;

/**
 * Created by tony on 2018/1/29.
 */
public class TestKafka {

    public static void main(String[] args) {

        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("acks", "all");
        producerProperties.put("retries", 0);
        producerProperties.put("batch.size", 16384);
        producerProperties.put("linger.ms", 1);
        producerProperties.put("buffer.memory", 33554432);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "com.cv4j.netdiscovery.extra.queue.kafka.RequestSerializer");

        Properties consumeProperties = new Properties();
        consumeProperties.put("bootstrap.servers", "localhost:9092");
        consumeProperties.put("group.id", "group");
        consumeProperties.put("enable.auto.commit", "true");
        consumeProperties.put("auto.commit.interval.ms", "1000");
        consumeProperties.put("auto.offset.reset", "earliest");
        consumeProperties.put("session.timeout.ms", "30000");
        consumeProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumeProperties.put("value.deserializer", "com.cv4j.netdiscovery.extra.queue.kafka.RequestDeserializer");

        KafkaQueue queue = new KafkaQueue(producerProperties,consumeProperties,"tony");

        Spider.create(queue)
                .name("tony")
                .url("http://www.163.com")
                .run();
    }
}
