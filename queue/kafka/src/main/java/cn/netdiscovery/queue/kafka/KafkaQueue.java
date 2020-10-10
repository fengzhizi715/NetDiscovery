package cn.netdiscovery.queue.kafka;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.AbstractQueue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by tony on 2018/1/28.
 */
public class KafkaQueue extends AbstractQueue {

    private Logger log = LoggerFactory.getLogger(KafkaQueue.class);

    private KafkaProducer<String, Request> producer;
    private KafkaConsumer<String, Request> consumer;
    private long timeout = 1000;
    private int partition = 0;
    private List<TopicPartition> topicPartitions;
    private long currentOffset = 0;

    public KafkaQueue(KafkaQueueConfig kafkaQueueConfig) {

        producer = new KafkaProducer<String, Request>(kafkaQueueConfig.getProducerProperties());
        consumer = new KafkaConsumer<>(kafkaQueueConfig.getConsumeProperties());

        this.topicPartitions = Optional.ofNullable(consumer.partitionsFor(kafkaQueueConfig.getTopicName()))
                .orElse(Collections.emptyList())
                .stream()
                .map(info -> new TopicPartition(info.topic(), info.partition()))
                .collect(Collectors.toList());

        log.info("kafka topicPartitions count: {}", topicPartitions.size());

        consumer.assign(this.topicPartitions);

    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        try {
            producer.send(new ProducerRecord<String, Request>(request.getSpiderName(), partition,null, request));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request poll(String spiderName) {
        // max.poll.records=1 强制消费一条数据
        ConsumerRecords<String, Request> records = consumer.poll(timeout);
        if (records!=null && records.iterator()!=null && records.count()>0) {

            consumer.commitAsync();
            ConsumerRecord<String, Request> record = records.iterator().next();
            log.info("kafka consumer result count: {}, data: {}", records.count(), record);

            this.currentOffset = record.offset();

            return record.value();
        }
        return null;
    }

    @Override
    public int getLeftRequests(String spiderName) {
        // TODO 计算不准
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
        int count = (int) topicPartitions.stream().mapToLong(tp -> {
            return endOffsets.get(tp) - this.currentOffset - 1;
        }).sum();
        log.info("kafka unconsumer count:" + count);
        return count;
    }
}
