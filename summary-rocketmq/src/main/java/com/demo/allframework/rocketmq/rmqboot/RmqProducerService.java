package com.demo.allframework.rocketmq.rmqboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Author YUDI-Corgi
 * @Description
 */
@Service
@Slf4j
public class RmqProducerService {

    @Resource
    private RocketMQTemplate mqTemplate;

    public <T> SendResult sendEasy(T msg, String topic, String group) {
        return this.send(msg, topic, group, null, -1);
    }

    public <T> SendResult sendWithTag(T msg, String topic, String group, String tag) {
        return this.send(msg, topic, group, tag, -1);
    }

    public <T> SendResult sendNotTag(T msg, String topic, String group, int delayLevel) {
        return this.send(msg, topic, group, null, delayLevel);
    }

    /**
     * 生产者发送消息
     * @param msg        消息内容
     * @param topic      消息实体保存的发送主题
     * @param group      消息实体保存的生产者所属组名
     * @param tag        消息Tag
     * @param delayLevel 延迟级别
     * @param <T>        消息内容类型
     * @return SendResult
     */
    private <T> SendResult send(T msg, String topic, String group, String tag, int delayLevel) {

        if (StringUtils.isBlank(topic) || StringUtils.isBlank(group)) {
            throw new RuntimeException("Topic | Group can not be empty.");
        }

        // 封装消息实体
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        RmqMessage<Object> mqMsg = new RmqMessage<>();
        mqMsg.setContent(msg);
        mqMsg.setTag(tag);
        mqMsg.setGroup(group);
        mqMsg.setTopic(topic);
        mqMsg.setMsgKey(uuid);
        mqMsg.setDelayLevel(delayLevel);

        // 构建 MQ 消息
        Message<RmqMessage<Object>> msgFinal = MessageBuilder.withPayload(mqMsg).setHeader("KEYS", uuid).build();

        // 生产者发送主题指定，格式：topic:tag，发送时会以冒号来分割destination，数组[0]即为topic，数组[1]即为tag
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            destination = topic + ":" + tag;
        }

        // 同步发送消息
        SendResult sendResult;
        if (delayLevel == -1) {
            sendResult = mqTemplate.syncSend(destination, msgFinal);
        } else {
            // 定时发送消息，timeout是指同步发送超时时间（单位:ms）
            sendResult = mqTemplate.syncSend(destination, msgFinal, 1000, delayLevel);
        }

        log.info("消息内容:{}, 发送结果:{}", mqMsg, sendResult);
        return sendResult;
    }

}
