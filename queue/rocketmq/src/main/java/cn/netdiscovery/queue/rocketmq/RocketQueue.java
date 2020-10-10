package cn.netdiscovery.queue.rocketmq;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.AbstractQueue;
import cn.netdiscovery.core.utils.SerializableUtils;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tony on 2019-07-16.
 */
public class RocketQueue extends AbstractQueue {

    private Producer producer;
    private Consumer consumer;
    private AtomicBoolean flag = new AtomicBoolean(false);

    public RocketQueue(Producer producer,Consumer consumer) {

        this.producer = producer;
        this.consumer = consumer;
        producer.start();
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        producer.send(request);

        if (flag.compareAndSet(false,true)) {
            consumer.subscribe(request.getSpiderName(),producer.getTags());
        }
    }

    @Override
    public Request poll(String spiderName) {

        Request request = null;

        MessageExt messageExt = consumer.getMessage(spiderName);

        if (messageExt!=null) {

            byte[] body = messageExt.getBody();
            request = deserialize(body);
        }

        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {

        Map<String, ConcurrentLinkedQueue<MessageExt>> map = consumer.getMap();
        int count = 0;

        for (String key : map.keySet()) {

            count += map.get(key).size();
        }

        return count;
    }

    private Request deserialize(byte[] data) {
        return SerializableUtils.fromJson(new String(data), Request.class);
    }
}
