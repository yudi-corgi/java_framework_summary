package com.demo.allframework.kafka.producer.consumer;

import com.demo.allframework.kafka.producer.interceptor.CustomConsumerInterceptor;
import com.demo.allframework.kafka.producer.strategy.RandomPartitionAssignor;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 消费者实例
 */
public class ConsumerEasyStart {

    public final static String KAFKA_BROKER = "localhost:9092";
    public final static String TOPIC_1 = "easy-start";
    public final static String TOPIC_2 = "easy-end";
    public final static String GROUP_ID = "group.demo";
    public final static AtomicBoolean IS_RUNNING = new AtomicBoolean(true);
    public final static Pattern TOPIC_PATTERN = Pattern.compile("easy-.*");

    /**
     * 配置参数
     * @return  Properties
     */
    public static Properties initConfig(){
        // 配置参数
        Properties props = new Properties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 是否自动提交位移
        // props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000)  自动提交间隔
        // 位移重置：latest 表示从下一条写入的消息的偏移量开始消费，earliest 表示从分区起始处的偏移量开始消费，none 表示不重置位移，找不到位移时会抛出异常
        // 若查找不到位移（如新的消费组在__consumer_offsets没有位移信息）或位移越界都会触发该重置操作
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "LocalConsumer");
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, CustomConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RandomPartitionAssignor.class.getName());
        return props;
    }

    public static void main(String[] args) {

        // 创建消费者客户端实例
        Consumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        try{
            // 以下三种订阅状态是互斥的，未订阅则为NONE，后两者具有消费者自动再均衡功能，会根据分区分配策略来自动分配各个消费者与分区的关系
            // 订阅方式一：assign()，订阅状态：USER_ASSIGNED
            // 获取主题的信息（名称、分区、副本等）并订阅主题的所有分区
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(TOPIC_1, Duration.ofSeconds(1));
            List<TopicPartition> tpList = new ArrayList<>();
            partitionInfos.forEach(p -> tpList.add(new TopicPartition(p.topic(), p.partition())));
            consumer.assign(tpList);
            // 订阅不同主题的具体分区
            consumer.assign(Arrays.asList(new TopicPartition(TOPIC_1, 0), new TopicPartition(TOPIC_2, 0)));

            // 订阅方式二：subscribe(Collection)，订阅状态：AUTO_TOPICS
            // 订阅主题，若多次调用subscribe()方法以最后一次调用订阅的主题为准
            consumer.subscribe(Arrays.asList(TOPIC_1,TOPIC_2));

            // 订阅方式三：subscribe(Pattern)，订阅状态：AUTO_PATTERN
            consumer.subscribe(TOPIC_PATTERN);

            // 循环消费事件
            while (IS_RUNNING.get()) {

                // 抓取记录，若在 1000ms 内没有抓取到记录则返回空
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                // 循环判断消费者是否分配了分区
                Set<TopicPartition> tpSet = new HashSet<>();
                while (tpSet.size() == 0) {
                    consumer.poll(Duration.ofSeconds(1));
                    tpSet = consumer.assignment();
                }
                // 根据时间获取位移，假设获取一天前的消息位置
                Map<TopicPartition, Long> timeStampToSearch = new HashMap<>(4);
                tpSet.forEach(tp -> timeStampToSearch.put(tp, System.currentTimeMillis() - 24L * 60 * 3600 * 1000));
                // offsetsForTimes() 会返回时间戳>=查询时间的第一条消息对应的偏移量和时间戳
                Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = consumer.offsetsForTimes(timeStampToSearch);
                // 指定一天前的消费位置，
                for (TopicPartition partition : tpSet) {
                    OffsetAndTimestamp offsetAndTimestamp = topicPartitionOffsetAndTimestampMap.get(partition);
                    if(offsetAndTimestamp != null){
                        consumer.seek(partition, offsetAndTimestamp.offset());
                    }
                }

                // 按分区粒度提交位移
                Set<TopicPartition> partitions = records.partitions();
                for (TopicPartition partition : partitions) {
                    List<ConsumerRecord<String, String>> recordsPartition = records.records(partition);
                    recordsPartition.forEach(System.out::println);
                    long lastConsumedOffset = recordsPartition.get(recordsPartition.size() - 1).offset();
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastConsumedOffset + 1)), Duration.ofSeconds(1));
                }

                // 获取消息集中指定主题的消息
                Iterable<ConsumerRecord<String, String>> tRecords = records.records(TOPIC_1);
                tRecords.forEach(System.out::println);
                // 异步提交位移
                consumer.commitAsync((offsets, exception) -> {
                    if(exception != null){
                        System.out.println("Fail to commit offset");
                        exception.printStackTrace();
                    }else{
                        System.out.println("Async commit successful.");
                    }
                });

                TopicPartition tp0 = new TopicPartition(TOPIC_1, 0);
                // 暂停该分区在拉取消息时返回数据
                consumer.pause(Collections.singleton(tp0));
                // 获取消息集中指定分区的消息
                List<ConsumerRecord<String, String>> tpRecords = records.records(tp0);
                tpRecords.forEach(System.out::println);
                // 恢复该分区在拉取消息时返回数据
                consumer.resume(Collections.singleton(tp0));

            }

            // 取消订阅，以下三者等效
            consumer.unsubscribe();
            consumer.subscribe(new ArrayList<>());
            consumer.assign(new ArrayList<>());

        } catch (WakeupException we) {
            // 不做处理，表示 consumer 可以关闭，异常是通过以下方法触发的
            // consumer.wakeup();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                // 保证消费者正常退出或再均衡之前同步提交位移，
                consumer.commitSync();
            }finally {
                consumer.close();
            }
        }
    }

}

