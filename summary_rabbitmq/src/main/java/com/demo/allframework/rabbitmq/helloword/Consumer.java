package com.demo.allframework.rabbitmq.helloword;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 * @author YUDI
 * @date 2020/12/27 18:05
 */
public class Consumer {

    public static void main(String[] args) {

        String queueName = "hello";

        // 创建连接工厂对象，指定 IP、Port 连接 MQ Server，也可以直接指定主机名称进行绑定
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        // factory.setHost("localhost");

        // 指定虚拟主机 VirtualHost、VirtualHost 对应的访问账户密码
        // 虚拟主机及访问用户可在 Web 管理平台/命令行进行配置
        factory.setVirtualHost("/ems");
        factory.setUsername("ems");
        factory.setPassword("123");

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            // 消费者队列的申明要和生产端保持一致
            channel.queueDeclare(queueName, false, false, false, null);
            // 接收消息
            channel.basicConsume(queueName, true, new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("消费者接收消息：" + new String(body, StandardCharsets.UTF_8));
                }
            });
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

}
