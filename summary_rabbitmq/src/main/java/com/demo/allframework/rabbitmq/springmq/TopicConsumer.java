package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author YUDI
 * @date 2020/12/29 0:00
 */
@Component
public class TopicConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,  // value 声明 @Queue 不指定任何信息代表创建临时队列
                    exchange = @Exchange(value = "topics", type = "topic", durable = "false"),// 指定交换机名称和类型
                    key = {"#.user.#", "order.#"}) // 指定 routingKey
    })
    public void topicsReceiveOne(String message){
        System.out.println("topic 消费者一：" + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,  // value 声明 @Queue 不指定任何信息代表创建临时队列
                    exchange = @Exchange(value = "topics", type = "topic", durable = "false"),// 指定交换机名称和类型
                    key = {"user.*"}) // 指定 routingKey
    })
    public void topicsReceiveTwo(String message){
        System.out.println("topic 消费者二：" + message);
    }

}
