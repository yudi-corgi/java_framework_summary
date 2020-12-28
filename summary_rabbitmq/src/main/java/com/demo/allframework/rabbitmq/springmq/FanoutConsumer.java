package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * fanout 广播模型消费者
 * @author YUDI
 * @date 2020/12/28 23:37
 */
@Component
public class FanoutConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,  // value 声明 @Queue 不指定任何信息代表创建临时队列
                    exchange = @Exchange(value = "notice", type = "fanout", durable = "false"))// 指定交换机名称和类型
    })
    public void fanoutReceiveOne(String message){
        System.out.println("fanout 消费者一：" + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,  // value 声明 @Queue 不指定任何信息代表创建临时队列
                    exchange = @Exchange(value = "notice", type = "fanout", durable = "false"))// 指定交换机名称和类型
    })
    public void fanoutReceiveTwo(String message){
        System.out.println("fanout 消费者二：" + message);
    }

}
