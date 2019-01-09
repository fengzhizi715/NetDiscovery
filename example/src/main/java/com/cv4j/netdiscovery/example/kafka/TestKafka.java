package com.cv4j.netdiscovery.example.kafka;

import com.alibaba.fastjson.JSONObject;
import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.pipeline.ConsolePipeline;
import com.cv4j.netdiscovery.example.jd.PricePipeline;
import com.cv4j.netdiscovery.extra.queue.kafka.KafkaQueue;
import com.cv4j.netdiscovery.extra.queue.kafka.KafkaQueueConfig;

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
        consumeProperties.put("auto.offset.reset", "earliest");
        consumeProperties.put("enable.auto.commit", "true");
        consumeProperties.put("auto.commit.interval.ms", "1000");
        consumeProperties.put("max.poll.records", "1");
        consumeProperties.put("session.timeout.ms", "30000");
        consumeProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumeProperties.put("value.deserializer", "com.cv4j.netdiscovery.extra.queue.kafka.RequestDeserializer");

        KafkaQueueConfig config = new KafkaQueueConfig.KafkaQueueConfigBuilder(producerProperties, consumeProperties)
                .topicName("tony")
                .build();

        KafkaQueue queue = new KafkaQueue(config);

        Request request = new Request("https://www.baidu.com").checkDuplicate(false);

        System.out.println(JSONObject.toJSONString(request));

        Spider.create(queue)
                .name("tony")
                .request(request)
                .pipeline(new ConsolePipeline())
                .run();
    }
}