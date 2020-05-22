package com.demo.allframework.rabbitmq.primitive;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

/**
 * 原生 RabbitMQ 消费端构建
 * @author YUDI
 * @date 2020/5/21 22:33
 */
public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        // 创建 ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();
        // 创建一个 Channel
        Channel channel = connection.createChannel();
        // 设置客户端最多接收未被ack的消息的个数
        channel.basicQos(1);
        // 队列名称
        String queueName = "person-queue2";
        // 不自动应答
        boolean autoAck = false;
        // true：表示不能将当前 Connection 中生产者发送的消息传送给当前 Connection 中的消费者
        boolean noLocal = false;
        // 声明当前队列为排它队列，该队列仅对首次声明它的连接可见，并在连接断开后自动删除
        boolean exclusive = false;
        // basicConsume 是一个同步方法
        channel.basicConsume(queueName,autoAck,"consumerTag",noLocal,exclusive,null,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                System.out.println("消费者获取信息："+ new String(body, StandardCharsets.UTF_8));
                System.out.println("消息下标为："+ deliveryTag);
                // 消息应答
                channel.basicAck(deliveryTag,true);
                // 消息拒绝
                // channel.basicReject(deliveryTag,true);
            }
        });

        // 监听连接或通道关闭原因，连接或通道为 close 状态时回调
        channel.addShutdownListener(e -> {
            // isHardError 区分是连接错误还是通道错误
            if(e.isHardError()){
                Connection c = (Connection) e.getReference();
                if (!e.isInitiatedByApplication()) {
                    System.out.println(c);
                    System.out.println("Connection 关闭原因：" + e.getReason().toString());
                }
            }else{
                Channel c = (Channel) e.getReference();
                ShutdownSignalException exception = c.getCloseReason();
                System.out.println(c);
                System.out.println("Channel 关闭原因：" + exception.getReason().toString() + "," +e.getCause());
            }
        });

        // 阻止主程序结束，让消费者持续接收消费信息
        LockSupport.park();

        // 关闭连接
        channel.close();
        connection.close();

    }
}
