package com.cv4j.netdiscovery.extra.queue.kafka;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by tony on 2018/1/28.
 */
public class KafkaQueue extends AbstractQueue {

    private KafkaProducer<String,Request> producer;
    private KafkaConsumer<String, Request> kafkaConsumer;
    private long timeout = 1000;

    public KafkaQueue(Properties producerProperties,Properties consumeProperties,String spiderName) {

        producer = new KafkaProducer<String, Request>(producerProperties);
        kafkaConsumer = new KafkaConsumer<>(consumeProperties);
        kafkaConsumer.subscribe(Arrays.asList(spiderName));
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        if (request.getSleepTime()>0) {

            timeout = request.getSleepTime();
        }
        producer.send(new ProducerRecord<String, Request>(request.getSpiderName(), request));
    }

    @Override
    public Request poll(String spiderName) {

        ConsumerRecords<String, Request> records = kafkaConsumer.poll(timeout);

        records.iterator().next().value();

        if (records!=null && records.iterator()!=null) {

            return records.iterator().next().value();
        }
        return null;
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return 0;
    }

    @Override
    public int getTotalRequests(String spiderName) {

        return getFilter().getTotalRequestsCount();
    }
}
