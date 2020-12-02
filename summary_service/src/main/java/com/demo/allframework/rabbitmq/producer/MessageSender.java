package com.demo.allframework.rabbitmq.producer;

import com.demo.allframework.rabbitmq.entity.Person;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生产者消息发送到 MQ Server（broker）
 *
 * @author YUDI
 * @date 2020/5/21 9:46
 */
@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendPersonMessage(Person person) {
        // 设置消息确认回调方法和消息返回回调方法
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        // 消息相关数据对象，通过设置 ID 标识当前消息的唯一性
        CorrelationData data = new CorrelationData();
        data.setId(person.getMessageId());
        /**
         *   exchange：指定消息要发送到的交换机
         *   routingKey：指定路由键，根据该键交换机会将消息放到绑定的队列
         *   object：消息实体，即 Message
         *   CorrelationData：消息相关数据对象
         */
        rabbitTemplate.convertAndSend("person-exchange", "person.test", person, data);
    }

    // 消息确认回调方法
    final ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        System.out.println("传递的ID：" + correlationData);
        if (ack) {
            System.out.println("消息投递成功");
        } else {
            System.out.println("消息投递失败");
        }
    };

    // 消息返回回调方法
    final RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> {
        System.out.println("返回消息："+message);
        System.out.println("反馈代码："+replyCode);
        System.out.println("反馈内容："+replyText);
        System.out.println("交换机："+exchange);
        System.out.println("路由键："+routingKey);
    };

}
