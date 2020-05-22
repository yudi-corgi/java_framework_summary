package com.demo.allframework.rabbitmq.consumer;

import com.demo.allframework.rabbitmq.entity.Person;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
 * 消费者消息接收
 * 此处只是为了简单实现例子，将 RabbitMQ 发送接收消息耦合在一个应用中
 * 实际情况应当是将接收和消费拆分为两个服务
 * @author YUDI
 * @date 2020/5/21 18:03
 */
@Component
public class MessageReceive {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value="person-queue",durable = "true"),
            exchange = @Exchange(name = "person-exchange", type = "topic"),
            key = "person.*"
        )
    )
    @RabbitHandler
    public void personMessageReceive(@Payload Person person,
                                     @Headers Map<String,Object> headers,
                                     Channel channel) throws IOException {
        //消费者操作
        System.out.println("-----------------开始消费信息，进行业务操作-------------------");
        System.out.println("Person 信息："+ person.getName());

        //设置 Qos 限流策略，
        //参数：单条消息大小（0 不限制） - 每次处理消息数量 - 是否为 consumer 级别，false 表示当前 channel 有效
        channel.basicQos(0,1,false);

        // 消费者应答，配置文件设置了 acknowledge-mode: manual（手动），表示消息已被消费
        // 参数一：  参数二：表示是否批量应答
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag,false);
    }

}
