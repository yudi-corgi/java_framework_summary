package com.demo.allframework.kafka.topic;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 主题管理，使用 KafkaAdminClient 对象操作 Kafka API
 */
public class TopicManager {

    private static final String KAFKA_BROKER = "localhost:9092";
    private static final String TOPIC_1 = "easy-admin";
    private static final String TOPIC_2 = "easy-assignments";

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        return properties;
    }

    public static void main(String[] args) {

        // 创建 KafkaAdminClient，该类线程安全，不建议直接引用，应通过以下方式创建
        AdminClient client = AdminClient.create(initConfig());

        // 指定主题的信息：名称、分区数、副本因子
        NewTopic nt1 = new NewTopic(TOPIC_1, 4, (short) 1);
        // 指定主题的信息：名称、分配方案
        Map<Integer, List<Integer>> replicasAssignments = new HashMap<>(2);
        // K（分区）- V（brokerId）
        replicasAssignments.put(0, Collections.singletonList(0));
        replicasAssignments.put(1, Collections.singletonList(0));
        NewTopic nt2 = new NewTopic(TOPIC_2, replicasAssignments);

        // 主题配置信息
        Map<String, String> configMap = new HashMap<>(2);
        configMap.put("cleanup.policy", "compact");
        nt2.configs(configMap);

        // 创建主题
        CreateTopicsResult result = client.createTopics(Arrays.asList(nt1, nt2));
        try {
            // 阻塞等待主题创建成功
            result.all().get();

            // 查看主题列表
            ListTopicsResult listTopicsResult = client.listTopics();
            // 输出所有主题名称
            listTopicsResult.names().get().forEach(System.out::println);
            // 输出所有主题名称及其是否为 Kafka 内部主题
            listTopicsResult.listings().get().forEach(System.out::println);
            // 输出所有主题名称:信息
            listTopicsResult.namesToListings().get().forEach((k, v) -> System.out.println(k + ":" + v.toString()));

            // 查看主题详细信息
            DescribeTopicsResult describeTopicsResult = client.describeTopics(Arrays.asList(TOPIC_1, TOPIC_2));
            describeTopicsResult.all().get().forEach((k, v) -> System.out.println(k + ":" + v.toString()));

            // 查看配置信息
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_1);
            DescribeConfigsResult configsResult = client.describeConfigs(Collections.singletonList(resource));
            configsResult.all().get().forEach((k, v) -> System.out.println(k + ":" + v.toString()));

            // 修改配置，并阻塞等待修改成功
            ConfigEntry entry = new ConfigEntry("cleanup.policy", "delete");
            AlterConfigOp config = new AlterConfigOp(entry, AlterConfigOp.OpType.SET);
            Map<ConfigResource, Collection<AlterConfigOp>> alterConfigMap = new HashMap<>(2);
            alterConfigMap.put(resource, Collections.singletonList(config));
            AlterConfigsResult alterConfigsResult = client.incrementalAlterConfigs(alterConfigMap);
            alterConfigsResult.all().get();

            // 修改主题分区
            List<List<Integer>> newAssignments = new ArrayList<>();
            newAssignments.add(Collections.singletonList(0));
            newAssignments.add(Collections.singletonList(0));
            // 参数：totoCount > 分区总数，必须比现有分区数大，否则报错
            // newAssignments > 新分区分配方案，集合长度为 totalCount 减 oldCount（旧分区分配不会变动），内部集合表示新分区副本分配的 brokerId，长度为副本因子
            NewPartitions newPartitions = NewPartitions.increaseTo(4, newAssignments);
            Map<String, NewPartitions> newPartitionsMap = Collections.singletonMap(TOPIC_1, newPartitions);
            CreatePartitionsResult partitions = client.createPartitions(newPartitionsMap);
            partitions.all().get();

            // 删除主题，并阻塞等待删除成功
            DeleteTopicsResult deleteTopicsResult = client.deleteTopics(Collections.singletonList(TOPIC_1));
            deleteTopicsResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

}
