package com.demo.allframework.rocketmq.rmqboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @Author YUDI-Corgi
 * @Description 消费者监听服务，实现 RocketMQListener，当主题有消息时，消费者会自动抓取消息从而执行 onMessage 方法
 */
@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "consumerGroup", topic = "testTopic")
public class RmqConsumerService implements RocketMQListener<RmqMessage<Object>> {

    @Override
    public void onMessage(RmqMessage<Object> objectRmqMessage) {
        log.info("监听到消息：{}", objectRmqMessage);
    }

}
