package com.demo.allframework.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @Author YUDI-Corgi
 * @Description 消费者
 */
public class Consumer {

    public static void main(String[] args) throws MQClientException {
        // 初始化 Push 消费者，并指定消费者组，Broker 会主动推送消息给消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("normal_consumer");
        // 指定 Namesrv 地址
        consumer.setNamesrvAddr("localhost:9876");
        // 指定消费者订阅的主题，参二是子主题，即消息队列，若为 null || "" || "*" 表示全部订阅
        consumer.subscribe("SyncTopic", "");
        // 指定每次拉取消息的数量，默认 32
        // 服务端判断已读取消息数量默认最大也是 32，因此要突破该值，除了在 Consumer 设置
        // 还需要在 broker 配置文件 broker.conf 指定参数 maxTransferCountOnMessageInMemory={number}
        consumer.setPullBatchSize(10);
        // 设置消费者每次抓取消息的最大数量，默认 1
        consumer.setConsumeMessageBatchMaxSize(10);
        // 注册消息监听器以处理抓取的消息，此处采用并发消费，也可以为有序消费
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
            // 这里直接返回了成功，实际业务中若消费失败应返回 RECONSUME_LATER，RocketMQ 会将消费失败的消息进行消费重试
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        // 启动消费者
        consumer.start();
        System.out.println("消费者启动...");
    }

}
