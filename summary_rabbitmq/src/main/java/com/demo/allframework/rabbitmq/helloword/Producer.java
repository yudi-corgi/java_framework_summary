package com.demo.allframework.rabbitmq.helloword;

import com.demo.allframework.rabbitmq.utils.CommonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

/**
 * 生产者
 * @author YUDI
 * @date 2020/12/27 18:04
 */
public class Producer {

    public static void main(String[] args) throws IOException {

        String queueName = "hello";
        String message = "Hello World!";

        // 通过连接工厂创建连接（Connection），通过连接创建通道（Channel）
        Connection connection = CommonUtil.getConnection();
        assert connection != null;
        Channel channel = connection.createChannel();

        /**
         * 声明队列，队列是幂等的，若不存在会自动创建
         * 参数一：队列名称
         * 参数二：durable 是否持久化队列到磁盘，true-是 false-否（注：仅仅是持久化队列，队列中的消息不会持久化）
         * 参数三：exclusive 是否独占，指当前队列是否仅当前连接通道可以声明绑定，true-是 false-否
         * 参数四：autoDelete 是否自动删除，指队列消息全部消费完后且没有消费者监听时是否删除队列，true-是 false-否
         * 参数五：arguments 附加参数信息
         */
        channel.queueDeclare(queueName, true, false, false, null);
        /**
         * 发送消息
         * 参数一：交换机名称，当没有声明时会自动生一个默认的交换机
         * 参数二：路由键，当交换机为默认时（即此处的“”），消息会发送到与路由键名称相同的队列
         * 参数三：传递消息的额外参数设置，此处声明队列中的消息进行持久化
         * 参数四：消息体，使用 byte[] 类型
         */
        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

        // 关闭连接
        CommonUtil.closeConnectionAndChannel(connection,channel);
    }

}
