package com.demo.allframework.rabbitmq.topics;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * topics 生产者
 * @author YUDI
 * @date 2020/12/27 23:50
 */
public class TopicProducer {

    public static void main(String[] args) throws IOException {
        String message = "Hello topics! Routing Key is ";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 声明交换机及其类型为：topic
        channel.exchangeDeclare("topics", BuiltinExchangeType.TOPIC);
        // 发布消息
        String routingKey = "user";
        channel.basicPublish("topics", routingKey, null, (message + routingKey).getBytes());

        // 关闭连接及通道
        CommonUtil.closeConnectionAndChannel(connection,channel);
    }

}
