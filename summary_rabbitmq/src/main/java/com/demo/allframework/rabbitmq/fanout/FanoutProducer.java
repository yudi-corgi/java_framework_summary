package com.demo.allframework.rabbitmq.fanout;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * fanout 生产者
 * @author YUDI
 * @date 2020/12/27 23:09
 */
public class FanoutProducer {

    public static void main(String[] args) throws IOException {
        String message = "Hello fanout!";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 声明交换机名称及其类型
        channel.exchangeDeclare("logs","fanout");
        // 发送消息
        channel.basicPublish("logs","",null, message.getBytes());

        // 关闭连接及通道
        CommonUtil.closeConnectionAndChannel(connection,channel);

    }

}
