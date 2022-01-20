package com.demo.allframework.rocketmq.practice;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

/**
 * @Author YUDI-Corgi
 * @Description 生产者、消费者构建器
 */
public class ProducerConsumerBuilder {

    public DefaultMQProducer producer;
    private final String NAMESRV = "119.91.138.217:9876";

    // region Producer

    /**
     * 获取生产者对象
     * @return DefaultMQProducer
     */
    public DefaultMQProducer getProducer() {
        if (producer == null) {
            buildProducer();
        }
        return producer;
    }

    /**
     * 生产者初始化
     */
    public void buildProducer() {
        producer = new DefaultMQProducer("combine_producer");
        producer.setNamesrvAddr(NAMESRV);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    // endregion

    // region Consumer

    public void initAndStartConsumer(String topic, String tags) {
        this.initAndStartConsumer("combine_consumer", topic, 32, tags, false);
    }

    public void initAndStartConsumer(String topic, String tags, boolean orderly) {
        this.initAndStartConsumer("combine_consumer", topic, 32, tags, orderly);
    }

    public DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, String topic, String tags) {
        return this.initAndStartConsumer(consumerGroup, topic, 32, tags, false);
    }

    public DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, int batchSize, String topic, String tags) {
        return this.initAndStartConsumer(consumerGroup, topic, batchSize, tags, false);
    }

    public DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, String topic, String tags, boolean orderly) {
        return this.initAndStartConsumer(consumerGroup, topic, 32, tags, orderly);
    }

    /**
     * 初始化并启动消费者
     * @param topic 主题
     * @param batchSize 每次消费消息的最大数量
     * @param tags 消息标签
     * @param orderly 是否顺序消息处理
     * @return DefaultMQPushConsumer
     */
    @SneakyThrows
    private DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, String topic, int batchSize, String tags, boolean orderly) {
        DefaultMQPushConsumer consumer;
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(NAMESRV);
        consumer.subscribe(topic, tags);
        consumer.setConsumeMessageBatchMaxSize(batchSize);
        if (orderly) {
            consumer.registerMessageListener(messageListenerOrderly());
        } else {
            consumer.registerMessageListener(messageListenerConcurrently());
        }
        consumer.start();
        return consumer;
    }

    /**
     * 并发消息监听器
     * @return MessageListenerConcurrently
     */
    private MessageListenerConcurrently messageListenerConcurrently() {
        return (msgList, context) -> {
            // do something...
            msgList.forEach(m -> {
                System.out.println("并发消费" + m.getTags());
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
    }

    /**
     * 顺序消息监听器
     * @return MessageListenerOrderly
     */
    private MessageListenerOrderly messageListenerOrderly() {
        return (msgList, context) -> {
            // do something...
            msgList.forEach(m -> {
                System.out.println("顺序消费" + m.getTags());
            });

            return ConsumeOrderlyStatus.SUCCESS;
        };
    }

    // endregion
}
