package cn.netdiscovery.queue.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

/**
 * Created by tony on 2019-07-16.
 */
public class Producer {

    private DefaultMQProducer producer;

    public Producer(String producerName,String nameServerAddress,int retryTimes) {
        this.producer = new DefaultMQProducer(producerName);
        this.producer.setNamesrvAddr(nameServerAddress);
        this.producer.setRetryTimesWhenSendAsyncFailed(retryTimes);
    }

    public void start() {

        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

}
