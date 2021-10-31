package com.demo.allframework.kafka.interceptor;

import com.demo.allframework.kafka.utils.BytesUtil;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义消费者拦截器
 */
public class CustomConsumerInterceptor implements ConsumerInterceptor<String, String> {

    private static final long EXPIRE_INTERVAL = 10000;

    /**
     * poll() 抓取消息后拦截调用该方法，判断消息的时间戳距当前时间戳是否超过 10s，超过则过滤，即 TTL 实现
     * @param records 抓取的消息记录
     * @return  过滤后未超时的消息
     */
    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        long now = System.currentTimeMillis();
        Map<TopicPartition, List<ConsumerRecord<String,String>>> unExpireMap = new HashMap<>();
        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, String>> unExpireList = new ArrayList<>();
            List<ConsumerRecord<String, String>> expireList = new ArrayList<>();
            List<ConsumerRecord<String, String>> trList = records.records(partition);
            trList.forEach(r -> {
                // 此处是每条消息相同 TTL 时间
                if (now - r.timestamp() < EXPIRE_INTERVAL) {
                    unExpireList.add(r);
                }
                // 此处是判断消息的Header中是否包含TTL字段以确定是否超时
                Iterable<Header> headers = r.headers().headers("ttl");
                if(headers != null && headers.iterator().hasNext()){
                    Header ttlHeader = headers.iterator().next();
                    long ttl = BytesUtil.bytesToLong(ttlHeader.value());
                    if (ttl > 0 && now - r.timestamp() < ttl * 1000) {
                        // 未超时
                        unExpireList.add(r);
                    } else {
                        // 超时，这里只是简单判断做添加而已
                        expireList.add(r);
                    }
                }
            });
            if (!unExpireList.isEmpty()) {
                unExpireMap.put(partition, unExpireList);
            }
        }
        return new ConsumerRecords<>(unExpireMap);
    }

    /**
     * 位移提交后调用该方法
     * @param offsets 主题分区及要提交的位移数值
     */
    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        offsets.forEach((tp, offset) -> System.out.println(tp + ":" + offset.offset()));
    }

    @Override
    public void close() { }

    @Override
    public void configure(Map<String, ?> configs) { }

}
