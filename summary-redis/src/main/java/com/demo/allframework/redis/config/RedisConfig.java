package com.demo.allframework.redis.config;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
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

}
