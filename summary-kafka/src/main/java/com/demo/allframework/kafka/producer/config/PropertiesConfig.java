package com.demo.allframework.kafka.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 生产者/消费者参数配置（只是抽出来配置相同参数，这样多个实例只需要配置各自所需的独特的参数）
 */
@Configuration
public class PropertiesConfig {

    private final static String KAFKA_BROKER = "localhost:9092";

    /**
     * 初始化生产者参数配置
     * @return 生产者参数配置
     */
    @Bean("producerProps")
    public Properties initProducerConfig(){
        // 配置参数
        Properties props = new Properties();
        // 设置 K-V 序列化方式为 String
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        // 设置 broker 地址
        props.put("bootstrap.servers", KAFKA_BROKER);
        return props;
    }

    /**
     * 初始化消费者参数配置
     * @return 生产者参数配置
     */
    @Bean("consumerProps")
    public Properties initConsumerConfig(){
        // 配置参数
        Properties props = new Properties();
        // 设置 K-V 序列化方式为 String
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        // 设置 broker 地址
        props.put("bootstrap.servers", KAFKA_BROKER);
        // 设置 group.id，此处固定，后续知道含义再做更改
        props.put("group.id","group.demo");
        return props;
    }

}
