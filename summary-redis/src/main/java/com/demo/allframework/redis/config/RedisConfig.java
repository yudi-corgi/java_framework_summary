package com.demo.allframework.redis.config;

import com.demo.allframework.redis.listener.MessageReceiver;
import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author YUDI-Corgi
 * @description Redis 配置类
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 键值序列化方式配置
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new FastJson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    /**
     * 自定义 LettuceClientConfiguration 同时保留默认自动配置的 bean 实现的回调接口
     * @return LettuceClientConfigurationBuilderCustomizer
     */
    @Bean
    public LettuceClientConfigurationBuilderCustomizer clientConfigurationBuilderCustomizer() {
        return new LettuceClientConfigurationBuilderCustomizer() {
            /**
             * 通过 LettuceClientConfigurationBuilder 自定义 LettuceClientConfiguration 配置选项
             * 如：是否使用 Ssl、超时时间、数据读取节点等
             * @param clientConfigurationBuilder 配置构建器
             */
            @Override
            public void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder) {
                /*
                    配置 Redis 读写分离，ReadFrom 用于定义数据读取的节点，有如下模式：
                        MASTER：默认模式，仅从当前主节点读取数据
                        MASTER_PREFERRED：优先从主节点读取数据，当主节点不可用时再从副本读取
                        REPLICA：仅从副本节点读取数据
                        REPLICA_PREFERRED：优先读取副本节点，当副本不可用时再从主节点读取
                        LOWEST_LATENCY：从最低延迟的群集的任何节点中读取
                        ANY：从集群的任何节点中读取
                        ANY_REPLICA：从集群的任何副本节点中读取
                 */
                clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
            }
        };
    }

    /**
     * 通过消息监听容器配置订阅信息
     * @param factory         连接工厂
     * @param messageListener 消息监听器
     * @return 监听容器
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory,
                                                   MessageListener messageListener, MessageListenerAdapter adapter) {

        // 创建消息监听容器，并配置 Redis 连接工厂
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        // **** 方式一：通过消息监听器配置订阅信息

        // 设置监听器与通道的映射
        // container.addMessageListener(messageListener, new ChannelTopic("demo"));

        // 模式订阅，* 是通配符，表示任意值，因此指定任意通道名称发送消息时，都会被对应监听器接收
        // container.addMessageListener(messageListener, new PatternTopic("*"));

        // **** 方式二：通过消息监听适配器配置订阅信息

        container.addMessageListener(adapter, new ChannelTopic("demo"));

        return container;
    }

    /**
     * 消息监听适配器
     */
    @Bean
    public MessageListenerAdapter adapter(MessageReceiver receiver) {
        // 指定监听消息的对象和消息处理方法名，方法名默认是 handleMessage
        return new MessageListenerAdapter(receiver, "receive");
    }


}
