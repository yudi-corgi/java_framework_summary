package com.demo.allframework.redis.listener;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author YUDI-Corgi
 * @description 通道订阅者
 */
// @Component
@AllArgsConstructor
public class Subscribe implements MessageListener{

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        // 反序列化获取消息内容
        Object msg = redisTemplate.getValueSerializer().deserialize(message.getBody());

        // 反序列化获取消息所属通道
        String channel = redisTemplate.getStringSerializer().deserialize(message.getChannel());

        // 反序列化获取消息所属模式
        String mode = redisTemplate.getStringSerializer().deserialize(pattern);

        System.out.println("通道：" + channel);
        System.out.println("内容：" + msg);
        System.out.println("模式：" + mode);
    }

}
