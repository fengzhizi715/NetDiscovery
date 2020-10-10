package cn.netdiscovery.queue.rocketmq;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.utils.SerializableUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Created by tony on 2019-07-16.
 */
public class Producer {

    private DefaultMQProducer producer;

    private String tags;

    public Producer(String producerName, String nameServerAddress, int retryTimes, String tags) {

        this.producer = new DefaultMQProducer(producerName);
        this.producer.setNamesrvAddr(nameServerAddress);
        this.producer.setRetryTimesWhenSendAsyncFailed(retryTimes);
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void start() {

        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void send(Request request) {

        Message msg = new Message(request.getSpiderName(), tags, SerializableUtils.toJson(request).getBytes());

        try {
            producer.send(msg);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
