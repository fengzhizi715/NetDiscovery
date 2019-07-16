package cn.netdiscovery.queue.rocketmq;

import lombok.Getter;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created by tony on 2019-07-17.
 */
public class Consumer {

    @Getter
    private DefaultMQPushConsumer consumer;

    public Consumer(String consumerName,String nameServerAddress) {

        this.consumer = new DefaultMQPushConsumer(consumerName);
        this.consumer.setNamesrvAddr(nameServerAddress);
    }

    public void subscribe(String topic,String tag) {

        try {
            consumer.subscribe(topic, tag);

            consumer.registerMessageListener(new MessageListenerConcurrently() {

                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                                ConsumeConcurrentlyContext context) {

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }
}
