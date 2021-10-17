package com.demo.allframework.kafka.producer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author CDY
 * @date 2021/10/17
 * @description consume-transform-produce（消费-转换-生产模式），事务消息从消费到生产的转换（批处理）
 */
public class TransactionConsumeTransformProduce {

    private final static String BOOTSTRAP_SERVER = "49.235.97.57:9092";
    private final static String TOPIC = "easy-c2p";

    private static Properties initConsumerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
        return properties;
    }

    private static Properties initProducerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactionalId");
        return properties;
    }

    public static void main(String[] args) {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConsumerProperties());
        KafkaProducer<String, String> producer = new KafkaProducer<>(initProducerProperties());

        // 初始化事务
        producer.initTransactions();
        while (true) {
            // 抓取消息，超时时间为 1s
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            if (!records.isEmpty()) {
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                // 开启事务
                producer.beginTransaction();
                try {
                    // 遍历分区里的消息
                    records.partitions().forEach(p -> {
                        List<ConsumerRecord<String, String>> partitionRecords = records.records(p);
                        partitionRecords.forEach(record -> {
                            // 取到消息执行业务逻辑处理...
                            // 消费 -> 生产
                            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC, record.key(), record.value());
                            producer.send(producerRecord);
                        });
                        // 获取当前分区最后一条消息的偏移量
                        long lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                        // 需要提交的消费位移（即最后一条消息的下条消息的偏移量）
                        offsets.put(p, new OffsetAndMetadata(lastConsumedOffset + 1));
                    });
                    // consume-transform-produce 模式使用此方式将生产和消费的消息进行批处理
                    // 将指定偏移量列表发送给消费者组协调器，并将这些偏移量标记为当前事务的一部分。 仅当事务成功提交时，这些偏移量才会被视为已提交
                    // 注意：消费端需要设置偏移量不自动提交(enable.auto.commit=false)，并且也不能通过 sync()、async()方法手动提交
                    producer.sendOffsetsToTransaction(offsets, consumer.groupMetadata());
                    // 提交事务
                    producer.commitTransaction();
                } catch (Exception e) {
                    System.out.println("consume-transform-produce exception:" + e);
                    // 中止事务（未提交的偏移量和消息都会回滚）
                    producer.abortTransaction();
                }
            }
        }
    }

}
