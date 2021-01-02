package com.demo.allframework.rabbitmq.springmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

    /**
     * 注解声明监听的队列即可，因为 RabbitMQConfig 已配置交换机与队列的绑定
     * @param message 接收的消息对象
     */
    @RabbitListener(queues = {"boot_queue"})
    public void configTopicReceive(Message message, Channel channel) throws IOException {
        try {
            System.out.println(new String(message.getBody()));
            System.out.println("处理业务...");
            // 故意抛出异常
            // int a = 3/0;
            /**
             * 消息确认，参数如下
             * deliveryTag：消息的唯一标识，单调递增的正整数
             * multiple：同时拒绝确认多条消息（小于等于参数一的所有标识消息）
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            /**
             * 消息处理失败时通过该方法拒绝确认消息，参数一二同上：
             * requeue：重回队列，true 表示消息重回队列，broker 会重新发送消息，false 则不重发
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }
}
