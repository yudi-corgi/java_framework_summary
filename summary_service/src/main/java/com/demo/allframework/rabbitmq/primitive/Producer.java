package com.demo.allframework.rabbitmq.primitive;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 原生 RabbitMQ 生产端构建
 * @author YUDI
 * @date 2020/5/21 22:33
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/");

        // 创建连接
        Connection connection = factory.newConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        // 声明一个交换机，并且为主题模式
        // channel.exchangeDeclare("person-exchange2","fanout",true,false,null);
        // 声明一个队列，参数：队列名称，durable、exclusive、autoDelete、arguments
        // channel.queueDeclare("person-queue2",false,false,false,null);

        // 将交换机与交换机绑定，参数：destination、source、routingKey，发送到 source 的消息会根据路由键转发到与 destination 交换机绑定的队列
        // channel.exchangeBind("person-exchange2","person-exchange","exchange-bind");
        // 将队列与交换机绑定
        // channel.queueBind("person-queue2","person-exchange2","");

        // 通过通道发送数据
        String msg = "生产端";
        for(int i=0;i<5;i++){
            /*
             参数：Exchange、routingKey、mandatory、immediate、Properties、Body
             mandatory：
                true：交换机无法通过路由键等找到一个符合的队列时，会调用 Basic.Return 命令将消息传回生产端
                false：消息直接丢弃
             immediate：
                true：交换机在路由到队列时若发现没有消费者，则消息不会存入队列
                      当交换机匹配的队列都没有消费者时，调用 Basic.Return 命令返回生产端
             */
            channel.basicPublish("person-exchange","exchange-bind",true,false,null,msg.getBytes());
        }
    }

}
