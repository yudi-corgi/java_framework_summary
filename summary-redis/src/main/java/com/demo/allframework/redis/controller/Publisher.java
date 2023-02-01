package com.demo.allframework.redis.controller;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author YUDI-Corgi
 * @description 发布者
 */
@RestController
public class Publisher {

    @Resource private RedisTemplate<String, String> redisTemplate;
    @Resource private RedisMessageListenerContainer container;
    @Resource private MessageListener subscribe;

    @GetMapping("/pub/{channel}")
    public void pub(@PathVariable("channel") String channel) {
        // 指定通道发布消息
        redisTemplate.convertAndSend(channel, "哈哈哈哈哈");
        redisTemplate.convertAndSend(channel, "哦哦哦哦哦");
    }

    @GetMapping("/sub")
    public void sub() {
        // 订阅新通道
        container.addMessageListener(subscribe, new ChannelTopic("test"));
    }

    @GetMapping("/unsub")
    public void unsub() {
        // 取关所有通道
        container.removeMessageListener(subscribe);
    }

    @GetMapping("/unsubByTopic/{channel}")
    public void unsubByTopic(@PathVariable("channel") String channel) {
        // 取关指定通道
        container.removeMessageListener(subscribe, new PatternTopic(channel));
    }
}

