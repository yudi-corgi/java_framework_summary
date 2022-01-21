package com.demo.allframework.rocketmq.practice;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.concurrent.*;

/**
 * @Author YUDI-Corgi
 * @Description 生产者、消费者构建器
 */
public class ProducerConsumerBuilder {

    public DefaultMQProducer producer;
    public TransactionMQProducer transactionProducer;
    private final String NAMESRV = "119.91.138.217:9876";

    // region Producer

    /**
     * 获取普通生产者对象
     * @return DefaultMQProducer
     */
    public DefaultMQProducer getProducer() {
        if (producer == null) {
            buildProducer();
        }
        return producer;
    }

    /**
     * 获取事务生产者对象
     * @return TransactionMQProducer
     */
    public TransactionMQProducer getTransactionProducer() {
        if (transactionProducer == null) {
            buildTransactionProducer();
        }
        return transactionProducer;
    }

    /**
     * 普通生产者初始化
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

    /**
     * 事务生产者初始化
     */
    public void buildTransactionProducer() {
        transactionProducer = new TransactionMQProducer("transaction_producer");
        transactionProducer.setNamesrvAddr(NAMESRV);
        try {
            transactionProducer.setExecutorService(initThreadPool());
            transactionProducer.setTransactionListener(new OrderTransactionMessageListener());
            transactionProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义事务生产者线程池
     * @return ExecutorService
     */
    public ExecutorService initThreadPool() {
        return new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000), r -> {
                    Thread thread = new Thread(r);
                    thread.setName("client-transaction-msg-check-thread");
                    return thread;
                });
    }

    // endregion

    // region Consumer

    public void initAndStartConsumer(String topic, MessageSelector sql) {
        this.initAndStartConsumer("combine_consumer", topic, 32, null, sql, false);
    }

    public void initAndStartConsumer(String topic, String tags) {
        this.initAndStartConsumer("combine_consumer", topic, 32, tags, null, false);
    }

    public void initAndStartConsumer(String topic, String tags, boolean orderly) {
        this.initAndStartConsumer("combine_consumer", topic, 32, tags, null, orderly);
    }

    public DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, String topic, String tags) {
        return this.initAndStartConsumer(consumerGroup, topic, 32, tags, null, false);
    }

    public DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, int batchSize, String topic, String tags) {
        return this.initAndStartConsumer(consumerGroup, topic, batchSize, tags, null, false);
    }

    public DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, String topic, String tags, boolean orderly) {
        return this.initAndStartConsumer(consumerGroup, topic, 32, tags, null, orderly);
    }

    /**
     * 初始化并启动消费者
     * @param topic 主题
     * @param batchSize 每次消费消息的最大数量
     * @param tags 消息标签
     * @param sql sql 表达式过滤
     * @param orderly 是否顺序消息处理
     * @return DefaultMQPushConsumer
     */
    @SneakyThrows
    private DefaultMQPushConsumer initAndStartConsumer(String consumerGroup, String topic, int batchSize,
                                                       String tags, MessageSelector sql, boolean orderly) {
        DefaultMQPushConsumer consumer;
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(NAMESRV);
        if (tags != null) {
            consumer.subscribe(topic, tags);
        } else if (sql != null) {
            consumer.subscribe(topic, sql);
        } else {
            consumer.subscribe(topic, "*");
        }
        // 每次消费的消息条数
        consumer.setConsumeMessageBatchMaxSize(batchSize);
        // 消费者消费起始位置
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式：集群或广播
        consumer.setMessageModel(MessageModel.BROADCASTING);
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
