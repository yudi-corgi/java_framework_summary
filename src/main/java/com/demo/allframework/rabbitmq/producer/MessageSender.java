package com.demo.allframework.rabbitmq.producer;

import com.demo.allframework.rabbitmq.entity.Person;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生产者消息发送到 MQ Server（broker）
 * @author YUDI
 * @date 2020/5/21 9:46
 */
@Component
public class MessageSender {

   @Autowired
   private RabbitTemplate rabbitTemplate;

   public void sendPersonMessage(Person person){
       // 消息相关数据对象，通过设置 ID 标识当前消息的唯一性
       CorrelationData data = new CorrelationData();
       data.setId(person.getMessageId());
       /**
        *   exchange：指定消息要发送到的交换机
        *   routingKey：指定路由键，根据该键交换机会将消息放到绑定的队列
        *   object：消息实体，即 Message
        *   CorrelationData：消息相关数据对象
        */
       rabbitTemplate.convertAndSend("person-exchange","person.test",person,data);
   }


}
