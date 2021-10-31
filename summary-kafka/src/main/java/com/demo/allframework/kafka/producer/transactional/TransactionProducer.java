package com.demo.allframework.kafka.producer.transactional;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @author CDY
 * @date 2021/10/16
 * @description 生产者客户端事务消息发送示例
 */
public class TransactionProducer {

    private final static String BOOTSTRAP_SERVER = "49.235.97.57:9092";
    private final static String TOPIC = "easy-client";

    private static Properties initProperties(){
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "producer1");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactionId");
        return properties;
    }

    public static void main(String[] args) {
        KafkaProducer<String,String> producer = new KafkaProducer<>(initProperties());
        // 初始化并开启事务
        producer.initTransactions();
        producer.beginTransaction();
        try {
            ProducerRecord<String, String> one = new ProducerRecord<>(TOPIC, "First record.");
            producer.send(one);
            ProducerRecord<String, String> two = new ProducerRecord<>(TOPIC, "Second record.");
            producer.send(two);
            ProducerRecord<String, String> third = new ProducerRecord<>(TOPIC, "Third record.");
            producer.send(third);
            // ...处理其它业务
            // 提交事务
            producer.commitTransaction();
        } catch (Exception e) {
            System.out.println("事务提交异常:" + e);
            // 中止事务
            producer.abortTransaction();
        } finally {
            producer.close();
        }
    }

}
