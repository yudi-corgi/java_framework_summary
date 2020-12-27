package com.demo.allframework.rabbitmq.direct;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * direct 生产者
 * @author YUDI
 * @date 2020/12/27 23:35
 */
public class DirectProducer {

    public static void main(String[] args) throws IOException {
        String message = "Hello direct! Routing Key is ";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 声明交换机名称及类型
        channel.exchangeDeclare("logs_direct","direct");
        // 发送消息
        String routingKey = "error";
        channel.basicPublish("logs_direct", routingKey, null, (message + routingKey).getBytes());

        // 关闭连接及通道
        CommonUtil.closeConnectionAndChannel(connection,channel);
    }

}
