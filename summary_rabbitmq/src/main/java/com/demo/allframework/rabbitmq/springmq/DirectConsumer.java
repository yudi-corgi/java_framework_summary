package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * direct 直连模型消费者
 * @author YUDI
 * @date 2020/12/28 23:52
 */
@Component
public class DirectConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(
                value = @Queue,  // value 声明 @Queue 不指定任何信息代表创建临时队列
                exchange = @Exchange(value = "logs", durable = "false"),// 默认 direct，指定交换机名称和类型
                key = {"info","error","warn"}) // 指定 routingKey
    })
    public void directReceiveOne(String message){
        System.out.println("direct 消费者一：" + message);
    }


    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,  // value 声明 @Queue 不指定任何信息代表创建临时队列
                    exchange = @Exchange(value = "logs", durable = "false"),// 指定交换机名称和类型
                    key = {"error"}) // 指定 routingKey 只为 error
    })
    public void directReceiveTwo(String message){
        System.out.println("direct 消费者二：" + message);
    }


    @RabbitListener(queues = "boot_direct_queue")
    public void configDirectReceive(Message message){
        System.out.println(new String(message.getBody()));
    }
}
