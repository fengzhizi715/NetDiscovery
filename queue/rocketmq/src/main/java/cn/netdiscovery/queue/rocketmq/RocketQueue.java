package cn.netdiscovery.queue.rocketmq;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.AbstractQueue;
import cn.netdiscovery.core.utils.SerializableUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

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

        String msgId = producer.getMessageId();

        if (msgId!=null) {

            try {
                MessageExt messageExt = consumer.getConsumer().viewMessage(spiderName, producer.getMessageId());
                byte[] body = messageExt.getBody();
                request = deserialize(body);
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }

        return request;
    }

    @Override
    public int getLeftRequests(String spiderName) {

        return producer.getMsgs().size();
    }

    private Request deserialize(byte[] data) {
        return SerializableUtils.fromJson(new String(data), Request.class);
    }
}
