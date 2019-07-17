package cn.netdiscovery.queue.rocketmq;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.utils.SerializableUtils;
import lombok.Getter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tony on 2019-07-16.
 */
public class Producer {

    private DefaultMQProducer producer;

    @Getter
    private String tags;

    @Getter
    private List<String> msgs;

    public Producer(String producerName,String nameServerAddress,int retryTimes,String tags) {

        this.producer = new DefaultMQProducer(producerName);
        this.producer.setNamesrvAddr(nameServerAddress);
        this.producer.setRetryTimesWhenSendAsyncFailed(retryTimes);
        this.tags = tags;
        this.msgs = Collections.synchronizedList(new LinkedList<String>());
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
            SendResult sendResult = producer.send(msg);
            msgs.add(sendResult.getMsgId());
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

    public String getMessageId() {

        String msgId = null;

        if (msgs.size()>0) {

            msgId = msgs.get(0);
            msgs.remove(0);
        }

        return msgId;
    }
}
