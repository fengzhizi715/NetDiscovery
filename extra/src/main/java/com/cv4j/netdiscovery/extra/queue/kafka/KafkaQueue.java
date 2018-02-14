package com.cv4j.netdiscovery.extra.queue.kafka;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.safframework.tony.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serializer;

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

    public KafkaQueue(Properties producerProperties, Properties consumeProperties, Serializer<String> keySerializer, Serializer<Request> valueSerializer,String spiderName) {

        producer = new KafkaProducer<String, Request>(producerProperties,keySerializer,valueSerializer);
        kafkaConsumer = new KafkaConsumer<>(consumeProperties);
        kafkaConsumer.subscribe(Arrays.asList(spiderName));
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        try {
            producer.send(new ProducerRecord<String, Request>(request.getSpiderName(), request));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request poll(String spiderName) {

        ConsumerRecords<String, Request> records = kafkaConsumer.poll(timeout);

        if (records!=null && records.iterator()!=null && records.count()>0) {

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
