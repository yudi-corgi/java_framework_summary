package com.demo.allframework.kafka.producer.stream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 单词计数器（官方示例）
 */
public class WordCountOfficial {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        // 从指定主题创建 KStream，与 KTable 可以互转
        KStream<String, String> textLines = builder.stream("easy-stream-input");
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(Materialized.as("counts-store"));
        // Produced 用于定义可选参数，KStream 使用 Produced 实例将流具体化为主题（即将流数据生产到主题中）
        wordCounts.toStream().to("easy-stream-output", Produced.with(Serdes.String(), Serdes.Long()));
        // 一种 Kafka 客户端，允许对来自一个或多个输入主题的输入执行连续计算，并将输出发送到零个、一个或多个输出主题。
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    /**
     * 上方代码 lambda 写法.
     */
    public void lambda(){
        // 获取 String 和 Long 类型的序列化/反序列化器
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        // StreamsBuilder 是 Topology（拓扑）构建器，Topology 表示 Kafka Streams 应用程序的流处理逻辑。
        StreamsBuilder builder = new StreamsBuilder();
        // KStream 是 Key-Value 对的记录流抽象，即每条记录都是独立的实体/事件，如两条记录<K:V1>，<K:V2>，此处从输入主题构建一个 KStream
        KStream<String, String> textLines = builder.stream(
                "easy-stream-input",
                // 通过键值（反）序列化器创建 Consumed，该对象用于构建 KStream、KTable、GlobalKTable 对象时定义可选参数
                Consumed.with(stringSerde, stringSerde)
        );

        // KTable 是来自主键表的 KTable 变更日志流抽象，该日志流中的每条记录都是对主键表的更新，以记录键作为主键，如KStream中两条记录<K:V1>,<K:V2>，会变成<K:V2>
        KTable<String, Long> wordCounts = textLines
                // Split each text line, by whitespace, into words.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                // Group the text words as message keys
                .groupBy((key, value) -> value)
                // Count the occurrences of each word (message key).
                .count();

        // 存储变更日志流到输出主题
        wordCounts.toStream().to("easy-stream-output", Produced.with(stringSerde, longSerde));
    }

}
