package com.demo.allframework.kafka.producer.strategy;

import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义分区分配策略
 */
public class RandomPartitionAssignor extends AbstractPartitionAssignor {

    /**
     * 自定义分区器的名称，需唯一
     * @return 分区器名称
     */
    @Override
    public String name() {
        return "random";
    }

    /**
     * 发送自定义数据到 leader
     * @param topics 消费者订阅的主题列表
     * @return 订阅用户数据，default 实现为返回 null
     */
    @Override
    public ByteBuffer subscriptionUserData(Set<String> topics) {
        return super.subscriptionUserData(topics);
    }

    /**
     * 给定分区计数和成员订阅，执行组分配
     * @param partitionsPerTopic 每个主题的分区数
     * @param subscriptions 消费组成员订阅信息
     * @return 从每个成员映射到分配给他们的分区列表
     */
    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic, Map<String, Subscription> subscriptions) {
        // 获取每个主题的所有消费者
        Map<String, List<String>> consumersPerTopic = consumersPerTopic(subscriptions);
        // map 用于保存每个消费者的分配信息
        Map<String, List<TopicPartition>> assignment = new HashMap<>(8);
        subscriptions.forEach((k, v) -> assignment.put(k, new ArrayList<>()));

        // 针对每个主题进行分区分配
        consumersPerTopic.forEach((topic, v) -> {
            // 获取当前主题的消费者者数量
            int consumerSize = v.size();
            // 获取主题的分区数
            Integer partitionNum = partitionsPerTopic.get(topic);
            if (partitionNum != null) {
                // 获取当前主题下的所有分区
                List<TopicPartition> partitions = AbstractPartitionAssignor.partitions(topic, partitionNum);
                partitions.forEach(tp->{
                    // 获取当前主题下的所有消费者中的随机一个
                    int index = new Random().nextInt(consumerSize);
                    String randomConsumer = v.get(index);
                    // 然后将该分区保存到 map 中（消费组成员分配信息）
                    assignment.get(randomConsumer).add(tp);
                });
            }
        });
        return assignment;
    }

    /**
     * 获取每个主题的所有消费者
     * @param subscriptions 消费组成员订阅信息
     * @return 主题映射到所有订阅它的成员列表
     */
    private Map<String, List<String>> consumersPerTopic(Map<String, Subscription> subscriptions){
        Map<String, List<String>> result = new HashMap<>(8);
        subscriptions.forEach((consumerId, value) -> value.topics().forEach(t -> put(result, consumerId, t)));
        return result;
    }

    private void put(Map<String, List<String>> result, String consumerId, String topic){
        if (result.get(topic) == null) {
            List<String> consumerList = new ArrayList<>();
            consumerList.add(consumerId);
            result.put(topic, consumerList);
        }else{
            result.get(topic).add(consumerId);
        }
    }

}
