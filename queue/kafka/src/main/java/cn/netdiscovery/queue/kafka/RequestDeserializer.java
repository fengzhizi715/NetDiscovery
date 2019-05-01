package cn.netdiscovery.queue.kafka;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.utils.SerializableUtils;
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
        return SerializableUtils.fromJson(new String(data),Request.class);
    }

    @Override
    public void close() {

    }
}
