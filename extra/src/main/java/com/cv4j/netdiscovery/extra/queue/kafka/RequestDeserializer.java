package com.cv4j.netdiscovery.extra.queue.kafka;

import com.cv4j.netdiscovery.core.domain.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by tony on 2018/1/29.
 */
public class RequestDeserializer implements Deserializer<Request> {

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public Request deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        Request request = null;
        try {
            request = mapper.readValue(data, Request.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return request;
    }

    @Override
    public void close() {

    }
}
