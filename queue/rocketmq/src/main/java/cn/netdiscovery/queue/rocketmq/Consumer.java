package cn.netdiscovery.queue.rocketmq;

import com.safframework.tony.common.utils.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tony on 2019-07-17.
 */
@Slf4j
public class Consumer {

    @Getter
    private DefaultMQPushConsumer consumer;

    private Map<String,List<MessageExt>> map;

    public Consumer(String consumerName,String nameServerAddress) {

        this.consumer = new DefaultMQPushConsumer(consumerName);
        this.consumer.setNamesrvAddr(nameServerAddress);
        this.map = new ConcurrentHashMap<>();
    }

    public void subscribe(String topic,String tag) {

        try {
            consumer.subscribe(topic, tag);

            consumer.registerMessageListener(new MessageListenerConcurrently() {

                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                                ConsumeConcurrentlyContext context) {

                    if (Preconditions.isNotBlank(msgs)) {
                        List<MessageExt> messages= map.get(topic);
                        if (Preconditions.isNotBlank(messages)) {
                            messages.addAll(msgs);
                        } else {
                            List<MessageExt> list = Collections.synchronizedList(new LinkedList<>());
                            list.addAll(msgs);
                            map.put(topic,list);
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }

    public MessageExt getMessage(String topic) {

        MessageExt result = null;

        List<MessageExt> messages= map.get(topic);
        if (Preconditions.isNotBlank(messages)) {

            result = messages.get(0);
            messages.remove(0);
        }

        return result;
    }
}
