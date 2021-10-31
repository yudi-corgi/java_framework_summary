package com.demo.allframework.kafka.producer.topic;

import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;

import java.util.Map;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 创建主题校验策略，客户端没有配置校验策略的参数，该策略实现应当是编译服务端源码加入该类，重新运行服务端才可生效
 */
public class TopicPolicy implements CreateTopicPolicy {

    /**
     * 校验策略，规则：分区数小于5，副本因子大于1
     * @param requestMetadata  请求元数据
     * @throws PolicyViolationException  校验不通过抛出的异常
     */
    @Override
    public void validate(RequestMetadata requestMetadata) throws PolicyViolationException {
        System.out.println(requestMetadata.topic());
        System.out.println(requestMetadata.configs());
        System.out.println(requestMetadata.numPartitions());
        System.out.println(requestMetadata.replicationFactor());
        System.out.println(requestMetadata.replicasAssignments());
        if(requestMetadata.numPartitions() != null && requestMetadata.numPartitions() >= 5){
            throw new PolicyViolationException("Topic partitions must be less than 5, received: " + requestMetadata.numPartitions());
        }
        if(requestMetadata.replicationFactor() != null && requestMetadata.replicationFactor() <= 1){
            throw new PolicyViolationException("Replication factor must be greater than 1, received: " + requestMetadata.replicationFactor());
        }
    }

    /**
     * Kafka 关闭时调用
     */
    @Override
    public void close() { }

    /**
     * Kafka 启动时调用
     * @param configs  配置信息
     */
    @Override
    public void configure(Map<String, ?> configs) { }

}
