package com.demo.allframework.rabbitmq.helloword;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 消费者
 * @author YUDI
 * @date 2020/12/27 18:05
 */
public class Consumer {

    public static void main(String[] args) throws IOException {

        String queueName = "hello";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 消费者队列的申明要和生产者保持一致
        channel.queueDeclare(queueName, false, false, false, null);
        /**
         * 接收消息
         * 参数一：要消费的队列名称
         * 参数二：开启消息的自动应答，true-开启 false-关闭，开启后拿到消息就会进行确定（注：该消息并不一定已被消费完毕）
         * 参数三：消费消息的回调接口
         */
        channel.basicConsume(queueName, true, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){
                System.out.println("消费者接收消息：" + new String(body, StandardCharsets.UTF_8));
            }
        });
    }

}
