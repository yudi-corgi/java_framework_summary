package com.demo.allframework.rabbitmq.workqueues;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * 任务生产者
 * @author YUDI
 * @date 2020/12/27 21:53
 */
public class WorkProducer {

    public static void main(String[] args) throws IOException {
        String queueName = "work";
        String message = "Heavy work...";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 声明队列并发布消息
        channel.queueDeclare(queueName, false, false, false, null);
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("", queueName, null, (i + "：" + message).getBytes());
        }

        // 关闭连接
        CommonUtil.closeConnectionAndChannel(connection,channel);
    }


}
