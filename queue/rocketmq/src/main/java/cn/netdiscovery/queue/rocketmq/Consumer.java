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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by tony on 2019-07-17.
 */
@Slf4j
public class Consumer {

    @Getter
    private DefaultMQPushConsumer consumer;

    @Getter
    private Map<String, ConcurrentLinkedQueue<MessageExt>> map;

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

                    synchronized (this) {
                        if (Preconditions.isNotBlank(msgs)) {

                            ConcurrentLinkedQueue<MessageExt> messages = map.get(topic);
                            if (Preconditions.isNotBlank(messages)) {
                                messages.addAll(msgs);
                            } else {
                                ConcurrentLinkedQueue<MessageExt> queue = new ConcurrentLinkedQueue<MessageExt>();
                                queue.addAll(msgs);
                                map.put(topic,queue);
                            }
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

        ConcurrentLinkedQueue<MessageExt> messages= map.get(topic);

        if (messages!=null && !messages.isEmpty()) {
            result = messages.poll();
        }

        return result;
    }
}
