package com.demo.allframework.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author YUDI-Corgi
 * @Description 批量发送生产者
 */
public class BatchProducer {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("batch_producer");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        String topic = "BatchTopic";
        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(topic, "Tag", "Batch01", "BatchBody01".getBytes(RemotingHelper.DEFAULT_CHARSET)));
        messageList.add(new Message(topic, "Tag", "Batch02", "BatchBody02".getBytes(RemotingHelper.DEFAULT_CHARSET)));
        messageList.add(new Message(topic, "Tag", "Batch03", "BatchBody02".getBytes(RemotingHelper.DEFAULT_CHARSET)));
        System.out.println("消息批量发送");
        System.out.println(producer.send(messageList));
        producer.shutdown();
    }

}
