package com.demo.allframework.kafka.producer.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 单词计数器
 */
public class WordCountApplication {

    private static Properties initStreamConfig(){
        Properties props = new Properties();
        // 指定 Streams 唯一标识符
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-line-split");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 键值对的（反）序列化器，Serdes 是构建（反）序列化器的工厂对象
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    public static void main(String[] args) {

        final StreamsBuilder builder = new StreamsBuilder();
        // 使用构建器从指定的输入主题构建 KStream，KStream 将从该主题不断生产记录
        KStream<String, String> source = builder.stream("easy-stream-input", Consumed.with(Serdes.String(), Serdes.String()));
        // 单词拆分，将 source 流作为其输入，并通过按顺序处理 source 流中的每个记录并将其值字符串分解为单词列表，并将每个单词作为新记录生成到输出，从而生成新流，即 words
        KTable<String, Long> count = source.flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .groupBy((key, value) -> value)
                // count 将记录流转为变更日志流，即 KTable
                .count(Materialized.as("counts-store"));
        // 将更改日志流写回主题，先转为记录流，然后输出到另一个主题
        count.toStream().to("easy-stream-output", Produced.with(Serdes.String(), Serdes.Long()));
        // 构建器创建 Topology（拓扑）
        final Topology topology = builder.build();
        System.out.println("Topology（拓扑）信息：" + topology.describe());

        // 通过 Topology 和 StreamsConfig 参数构建 Streams 客户端
        final KafkaStreams streams = new KafkaStreams(topology, initStreamConfig());
        streams.start();

        // 线程计数器
        final CountDownLatch latch = new CountDownLatch(1);
        // 添加 shutdown 钩子
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook"){
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            // 启动客户端执行
            streams.start();
            // 阻塞等待执行完毕
            latch.await();
        }catch (Throwable e){
            System.exit(1);
        }
        System.exit(0);
    }

}
