package com.cv4j.netdiscovery.example.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * Created by tony on 2018/2/14.
 */
public class SimplePartitioner implements Partitioner {

    public void configure(Map<String, ?> configs) {}

    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        int numberOfpartitons =cluster.partitionsForTopic(topic).size();
        return key.hashCode()%numberOfpartitons;
    }

    public void close() {}
}
