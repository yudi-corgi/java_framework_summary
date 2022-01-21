package com.demo.allframework.rocketmq.practice;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author YUDI-Corgi
 * @Description 模拟订单事务消息监听器
 */
public class OrderTransactionMessageListener implements TransactionListener {

    private final ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();
    private final HashMap<String, String> orderMockMap = new HashMap<>();
    private final static int MAX_COUNT = 5;
    private final Random random = new Random();

    @SneakyThrows
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        System.out.println("执行本地事务...");
        // 获取订单的唯一业务单号
        String bizUniNo = msg.getUserProperty("bizUniNo");

        // ... 模拟执行入库操作， insert into ...
        int one = random.nextInt(16);
        int two = random.nextInt(16);
        if (one > two) {
            orderMockMap.put(bizUniNo, "Store success");
            TimeUnit.SECONDS.sleep(1);
        }

        // 返回 UnKNOW，让 MQ 进入回查阶段
        System.out.println("本地事务状态返回 UnKNOW...");
        return LocalTransactionState.UNKNOW;
    }

    @SneakyThrows
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String bizUniNo = msg.getUserProperty("bizUniNo");

        // ...模拟查询 bizUniNo 订单
        String val = orderMockMap.get(bizUniNo);
        TimeUnit.SECONDS.sleep(1);

        // 存在返回 COMMIT_MESSAGE
        if (val != null) {
            System.out.println("本地事务回查成功，返回 Success");
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        // 不存在，记录重试次数，未超过则返回 UnKNOW，超过则 Rollback
        return rollbackOrUnKnow(bizUniNo);
    }

    public LocalTransactionState rollbackOrUnKnow(String bizUniNo){
        System.out.println("执行事务消息回查...");
        Integer num = countMap.get(bizUniNo);
        if (num == null) {
            num = 1;
        } else if (++num > MAX_COUNT) {
            countMap.remove(bizUniNo);
            System.out.println("事务回查返回 ROLLBACK");
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        countMap.put(bizUniNo, num);
        System.out.println("事务回查返回 UnKNOW");
        return LocalTransactionState.UNKNOW;
    }
}
