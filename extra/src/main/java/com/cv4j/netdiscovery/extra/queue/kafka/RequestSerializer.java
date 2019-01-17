package com.cv4j.netdiscovery.extra.queue.kafka;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.utils.SerializableUtils;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by tony on 2018/1/29.
 */
public class RequestSerializer implements Serializer<Request> {

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Request data) {
        return SerializableUtils.toJson(data).getBytes();
    }

    @Override
    public void close() {

    }
}
