package com.demo.allframework.rabbitmq.workqueues;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 任务消费者二
 * @author YUDI
 * @date 2020/12/27 21:53
 */
public class WorkConsumerTwo {

    public static void main(String[] args) throws IOException {

        String queueName = "work";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 声明并监听队列
        channel.queueDeclare(queueName, false, false, false, null);
        // 设置通道一次只接收一条未确认的消息
        channel.basicQos(1);
        channel.basicConsume(queueName, false, new DefaultConsumer(channel){
            @SneakyThrows
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("消费者二接收消息：" + message);
                try{
                    CommonUtil.doWork(message);
                }finally {
                    System.out.println("Work done!");
                }
                // 参数一：当前消息标识
                // 参数二：是否同时确认多条消息，false-表示一次确认一条
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
    }

}
