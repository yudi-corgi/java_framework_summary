package com.demo.allframework.rocketmq.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.TimeUnit;

/**
 * @Author YUDI-Corgi
 * @Description 单向发送生产者
 */
public class OnewayProducer {

    public static void main(String[] args) throws Exception {
        // 初始化生产者实例，并指定所属的生产者组
        DefaultMQProducer producer = new DefaultMQProducer("oneway_producer");
        // 指定 Namesrv 地址
        producer.setNamesrvAddr("localhost:9876");
        // 启动生产者实例
        producer.start();
        int count = 100;
        for (int i = 0; i < count; i++) {
            Message msg = new Message("OnewayTopic", "OnewayTag" + i, ("Oneway" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 单向发送不会等待 broker 确认收到才返回
            producer.sendOneway(msg);
        }
        // 休眠 5s 等待消息发送结束
        TimeUnit.SECONDS.sleep(5);
        producer.shutdown();
    }

}
