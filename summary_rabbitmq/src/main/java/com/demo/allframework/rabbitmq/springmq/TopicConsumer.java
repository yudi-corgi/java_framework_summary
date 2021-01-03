package com.demo.allframework.rabbitmq.springmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    @RabbitListener(queues = {"boot_topic_queue"})
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
            TimeUnit.SECONDS.sleep(2);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            /**
             * 消息处理失败时通过该方法拒绝确认消息，参数一二同上：
             * requeue：重回队列，true 表示消息重回队列，broker 会重新发送消息，false 则不重发
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            // basicReject 等同于 basicNack，但无法同时拒绝确认多条消息，参数二为 boolean requeue
            // channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    /**
     * 监听设置死信队列的普通队列
     * 如果不监听普通队列，发送到该队列的消息只要符合死信情况，都会路由到死信队列，因此可以构成延迟队列的效果
     * 延迟队列概念：消息进入队列后不会立即被消费，只有达到指定时间后才会被消费。
     * 实现本质：TTL + 死信队列
     */
    @RabbitListener(queues = {"test_topic_queue"})
    public void testTopicConsumer(Message message, Channel channel) throws IOException, InterruptedException {
        System.out.println("拒绝签收并且不重回队列.");
        TimeUnit.SECONDS.sleep(1);
        // 死信情况测试三：消息拒绝确认并且不返回队列
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
    }

    /**
     * 死信队列监听，如果绑定死信队列的普通队列没被监听，并且是超时产生的死信，则效果等同于延迟队列
     */
    @RabbitListener(queues = {"dlx_queue"})
    public void dlxConsumer(Message message, Channel channel) throws IOException {
        System.out.println("接收死信队列消息.");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
    }

}
