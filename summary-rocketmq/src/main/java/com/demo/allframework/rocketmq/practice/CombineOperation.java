package com.demo.allframework.rocketmq.practice;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * @Author YUDI-Corgi
 * @Description 生产者、消费者综合操作示例
 */
public class CombineOperation {

    private static final ProducerConsumerBuilder PC_BUILDER = new ProducerConsumerBuilder();
    public static DefaultMQProducer producer = PC_BUILDER.getProducer();
    public static TransactionMQProducer txProducer = PC_BUILDER.getTransactionProducer();
    public static String[] tags = {"TagA","TagB","TagC","TagD","TagE"};

    @SneakyThrows
    public static void main(String[] args) {
        CombineOperation co = new CombineOperation();

        // 选择消息队列发送
        co.selectMessageQueueToSend(100);

        // TAG 表达式过滤 - 启动两个消费者
        co.filterMessageByTags(10);
        PC_BUILDER.initAndStartConsumer("TagsFilterCG","filterByTagsTopic", tags[0], true);
        PC_BUILDER.initAndStartConsumer("TagsFilterCG","filterByTagsTopic", tags[0], false);
        PC_BUILDER.initAndStartConsumer("filterByTagsTopic", tags[0] + "||" + tags[1], false);

        // SQL 表达式过滤
        co.filterMessageBySql();
        PC_BUILDER.initAndStartConsumer("filterBySqlTopic", MessageSelector.bySql("status is not null and id > 1"));

        // 发送事务消息
        co.sendTransactionMsg();
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
                msg = new Message("filterByTagsTopic", tags[0], "testId",
                        "filter by tags".getBytes(RemotingHelper.DEFAULT_CHARSET));
            } else {
                msg = new Message("filterByTagsTopic", tags[1], "testId",
                        "filter by tags".getBytes(RemotingHelper.DEFAULT_CHARSET));
            }
            System.out.printf("%s%n", producer.send(msg));
        }
    }

    /**
     * 用于 SQL 表达式模式过滤此类消息
     */
    @SneakyThrows
    public void filterMessageBySql() {
        Message msg = new Message("filterBySqlTopic", tags[2], "filter by sql".getBytes(RemotingHelper.DEFAULT_CHARSET));
        // 添加消息属性
        msg.putUserProperty("status", "1");
        msg.putUserProperty("id", "123");
        SendResult result = producer.send(msg);
        System.out.printf("%s%n", result);
    }

    @SneakyThrows
    public void sendTransactionMsg() {
        Message msg = new Message("transactionTopic", tags[3], "Create order".getBytes(RemotingHelper.DEFAULT_CHARSET));
        msg.putUserProperty("bizUniNo", "9527");
        TransactionSendResult result = txProducer.sendMessageInTransaction(msg, null);
        System.out.printf("%s%n", result);
    }
}
