package com.cv4j.netdiscovery.extra.queue.kafka;

import com.cv4j.netdiscovery.core.domain.Request;
import com.google.gson.Gson;
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
        Gson gson = new Gson();
        return gson.fromJson(new String(data), Request.class);
    }

    @Override
    public void close() {

    }
}
