package com.demo.allframework.rocketmq.producer;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @Author YUDI-Corgi
 * @Description 同步生产者
 */
public class SyncProducer {

    public static void main(String[] args) throws Exception {
        // 创建默认生产者，并指定所属的生产者组，该实例对象线程安全
        DefaultMQProducer producer = new DefaultMQProducer("sync_producer");
        // 指定 Namesrv 地址
        producer.setNamesrvAddr("119.91.138.217:9876");
        // 启动生产者实例
        producer.start();
        // 循环发送消息
        int count = 100;
        for (int i = 0; i < count; i++) {
            // 构建消息对象
            // 参一：发送的主题；参二：消息的标签（便于查询）；参三：消息内容
            StopWatch sw = new StopWatch();
            Message msg = new Message("SyncTopic", "SyncTag" + i, "Hello RocketMQ".getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 同步发送消息，内部含有重试机制，默认重试 2 次
            // SendResult 是消息发送成功后返回的结果对象，含有发送状态、MsgId、消息队列、队列偏移量等信息
            SendResult sr = producer.send(msg);
            // 指定发送的超时时间，单位 ms
            //SendResult sr = producer.send(msg, 1000);
            System.out.printf("%s%n", sr);
        }
        // 关闭生产者并释放相应资源
        producer.shutdown();
    }

}
