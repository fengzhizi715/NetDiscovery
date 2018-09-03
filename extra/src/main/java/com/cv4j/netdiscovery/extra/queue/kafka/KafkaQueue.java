package com.cv4j.netdiscovery.extra.queue.kafka;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;

/**
 * Created by tony on 2018/1/28.
 */
public class KafkaQueue extends AbstractQueue {

    private KafkaProducer<String,Request> producer;
    private KafkaConsumer<String, Request> consumer;
    private long timeout = 1000;
    private int partition = 0;

    public KafkaQueue(KafkaQueueConfig kafkaQueueConfig) {

        producer = new KafkaProducer<String, Request>(kafkaQueueConfig.getProducerProperties());
        consumer = new KafkaConsumer<>(kafkaQueueConfig.getConsumeProperties());

        if (kafkaQueueConfig.getPartition()>0) {

            partition = kafkaQueueConfig.getPartition();
        }

        TopicPartition topicPartition = new TopicPartition(kafkaQueueConfig.getTopicName(), partition);
        consumer.assign(Arrays.asList(topicPartition));
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        try {
            producer.send(new ProducerRecord<String, Request>(request.getSpiderName(),partition,null, request));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request poll(String spiderName) {

        ConsumerRecords<String, Request> records = consumer.poll(timeout);

        if (records!=null && records.iterator()!=null && records.count()>0) {

            return records.iterator().next().value();
        }

        return null;
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return 0;
    }
}
