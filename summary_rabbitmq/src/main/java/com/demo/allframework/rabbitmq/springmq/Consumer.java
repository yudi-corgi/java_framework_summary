package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Spring 整合 RabbitMQ 消费者
 * @author YUDI
 * @date 2020/12/28 23:03
 */
@Component
// 该注解作用在类上，表示监听 hello 队列，若只定义名称，默认创建：持久化、非独占、非自动删除队列
// 配合 @RabbitHandler 注解的方法表示都会接收 hello 队列的消息
@RabbitListener(queuesToDeclare = {@Queue(value = "hello",durable = "false", exclusive = "false", autoDelete = "false")})
public class Consumer {

    // 该注解表示监听队列消息后由该方法进行回调消费
    @RabbitHandler
    public static void receive(Object message){
        System.out.println("接收消息：" + message);
    }


}



