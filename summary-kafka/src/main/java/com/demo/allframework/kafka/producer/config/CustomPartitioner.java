package com.demo.allframework.kafka.producer.config;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义分区器
 */
public class CustomPartitioner implements Partitioner {

    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * <p>
     * DefaultPartitioner 默认分区器会在 key 为 null 时轮询可用的分区发送消息，
     * 此处修改为轮询所有的分区
     * </p>
     * @param topic  主题
     * @param key  键
     * @param keyBytes  序列化后的键
     * @param value  值
     * @param valueBytes  序列化后的值
     * @param cluster  集群对象
     * @return  分区号
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // 获取主题分区信息
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        partitionInfos.forEach(System.out::println);
        // 获取主题的分区数
        Integer numPartitions = cluster.partitionCountForTopic(topic);
        if(keyBytes == null){
            return counter.getAndIncrement() % numPartitions;
        }else{
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    @Override
    public void close() { }

    @Override
    public void configure(Map<String, ?> configs) { }

}
