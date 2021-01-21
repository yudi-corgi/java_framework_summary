package com.demo.allframework.rabbitmq.haproxy;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * HaProxy 测试集群节点的负载均衡
 * @author YUDI
 * @date 2021/1/21 21:56
 */
public class LoadBalance {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        // 指定 HaProxy 的 IP/Port，在 HaProxy 配置文件配置 rabbitmq 节点及负载均衡策略
        factory.setHost("192.168.137.7");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("hello_haProxy",true,false,false,null);
        channel.basicPublish("","hello_haProxy",null, "Hello, HaProxy!".getBytes());
        channel.close();
        connection.close();
        System.out.println("send success!!!");
    }

}
