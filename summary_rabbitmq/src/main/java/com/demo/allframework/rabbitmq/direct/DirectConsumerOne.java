package com.demo.allframework.rabbitmq.direct;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * direct 消费者一
 * @author YUDI
 * @date 2020/12/27 23:35
 */
public class DirectConsumerOne {

    public static void main(String[] args) throws IOException {

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 创建临时队列，是一个非持久化、独占、自动删除的队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定交换机和队列，路由键绑定为：info
        channel.queueBind(queueName,"logs_direct","info");
        // 接收消息
        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者一接收信息：" + new String(body, StandardCharsets.UTF_8));
            }
        });

    }

}
