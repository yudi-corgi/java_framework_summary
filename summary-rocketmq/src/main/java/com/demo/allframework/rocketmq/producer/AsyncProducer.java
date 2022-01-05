package com.demo.allframework.rocketmq.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author YUDI-Corgi
 * @Description 异步生产者
 */
public class AsyncProducer {

    public static void main(String[] args) throws Exception {
        // 初始化生产者实例，并指定所属的生产者组
        DefaultMQProducer producer = new DefaultMQProducer("async_producer");
        // 指定 Namesrv 地址
        producer.setNamesrvAddr("localhost:9876");
        // 启动生产者实例
        producer.start();
        // 设置失败重试次数为 0，默认是 2
        producer.setRetryTimesWhenSendFailed(0);
        int count = 100;
        // 初始化计数器
        final CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            try {
                final int index = i;
                Message msg = new Message("AsyncTopic", "AsyncTag" + i, ("MockOrder" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                // 异步发送
                producer.send(msg, new SendCallback() {
                    // 发送成功时的回调
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                    }

                    // 发送失败抛出异常时的回调
                    @Override
                    public void onException(Throwable e) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 阻塞 5s 等待消息异步发送结束
            countDownLatch.await(5, TimeUnit.SECONDS);
            producer.shutdown();
        }
    }

}
