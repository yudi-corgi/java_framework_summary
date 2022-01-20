package com.demo.allframework.rocketmq.practice;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author YUDI-Corgi
 * @Description 生产者、消费者综合操作示例
 */
public class CombineOperation {

    private static final ProducerConsumerBuilder PC_BUILDER = new ProducerConsumerBuilder();
    public static DefaultMQProducer producer = PC_BUILDER.getProducer();
    public String[] tags = {"TagA","TagB","TagC","TagD","TagE"};

    @SneakyThrows
    public static void main(String[] args) {
        CombineOperation co = new CombineOperation();
        //co.selectMessageQueueToSend(100);
        co.filterMessageByTags(10);

        TimeUnit.SECONDS.sleep(1);

        // 启动两个消费者
        PC_BUILDER.initAndStartConsumer("filterByTagsTopic", "tags[0]", true);
        PC_BUILDER.initAndStartConsumer("filterByTagsTopic", "tags[1]", false);

    }

    /**
     * 指定发送的消息队列
     * @param count 循环次数
     */
    @SneakyThrows
    public void selectMessageQueueToSend(int count) {
        for (int i = 0; i < count; i++) {
            int flag = i % 10;
            Message msg = new Message("selectMqTopic", tags[i % tags.length], "KEY" + i,
                    ("selectMQ" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    // arg 参数即 send 方法的第三个参数
                    int id = (int) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, flag);
            System.out.printf("%s%n", result);
        }
    }

    /**
     * 根据 TAGS 参数过滤消息
     * @param count 循环次数
     */
    @SneakyThrows
    public void filterMessageByTags(int count) {
        for (int i = 0; i < count; i++) {
            Message msg;
            if (i % 2 == 0) {
                msg = new Message("filterByTagsTopic", "tags[0]", "testId",
                        "filter by tags".getBytes(RemotingHelper.DEFAULT_CHARSET));
            } else {
                msg = new Message("filterByTagsTopic", "tags[1]", "testId",
                        "filter by tags".getBytes(RemotingHelper.DEFAULT_CHARSET));
            }
            System.out.printf("%s%n", producer.send(msg));
        }
    }

}
