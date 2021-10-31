package com.demo.allframework.kafka.producer.producer;

import com.demo.allframework.kafka.producer.config.CustomPartitioner;
import com.demo.allframework.kafka.producer.interceptor.CustomPrefixProducerInterceptor;
import com.demo.allframework.kafka.producer.interceptor.CustomSuffixProducerInterceptor;
import com.demo.allframework.kafka.producer.utils.BytesUtil;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 生产者实例
 */
public class ProducerEasyStart {

    public final static String KAFKA_BROKER = "localhost:9092";
    public final static String TOPIC = "easy-start";

    /**
     * 配置参数
     * @return  Properties
     */
    public static Properties initConfig(){
        // 配置参数
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, CustomPrefixProducerInterceptor.class.getName() + ","
                + CustomSuffixProducerInterceptor.class.getName());
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.ACKS_CONFIG, "-1");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "LocalProducer");
        return props;
    }

    public static void main(String[] args) {

        // 配置生产者客户端参数并创建 KafkaProducer 实例
        Producer<String, String> producer = new KafkaProducer<>(initConfig());
        // 构建需要发送的消息，并在消息的Header字段添加TTL信息，以便消费端拦截器根据该字段判断是否过期
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, 0, System.currentTimeMillis(), "key","Easy start3!",
                new RecordHeaders().add(new RecordHeader("ttl", BytesUtil.longToByte(5))));

        try {
            // 同步发送消息一
            producer.send(record).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            // 同步发送消息二，一和二其实是相同的，Future 是异步计算的结果，通过 get() 方法阻塞线程等待 Kafka 的响应
            Future<RecordMetadata> metadata = producer.send(record);
            // get() 也可通过参数实现阻塞超时
            RecordMetadata recordMetadata = metadata.get(3, TimeUnit.SECONDS);
            System.out.println("元数据：" + recordMetadata.toString());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        // 异步发送记录到主题，参数二：回调函数，发送到服务器的记录已被确认时，将调用此方法
        producer.send(record, (metadata, exception) -> {
            if(exception != null){
                System.out.println("确认异常：" + exception);
                System.out.println("主题分区：" + metadata.partition());
                System.out.println("开始执行重发逻辑 Retrying...");
            }else{
                System.out.println("主题名：" + metadata.topic());
                System.out.println("分区值：" + metadata.partition());
                System.out.println("偏移量：" + metadata.offset());
                System.out.println("时间戳："+ metadata.timestamp());
                System.out.println("回调：记录发送成功");
            }
        });

        // 关闭生产者实例
        producer.close();
    }

}
