package com.demo.allframework.rabbitmq.fanout;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * fanout 消费者三
 * @author YUDI
 * @date 2020/12/27 23:10
 */
public class FanoutConsumerThree {

    public static void main(String[] args) throws IOException {
        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        // 创建临时队列，是一个非持久化、独占、自动删除的队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定交换机和队列
        // 参数一：队列名称  参数二：交换机名称  参数三：路由键，广播模式不需要，设置为 "" 即可
        channel.queueBind(queueName,"logs","");
        // 接收消息
        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者三接收信息：" + new String(body, StandardCharsets.UTF_8));
            }
        });
    }

}
