package com.demo.allframework.redis.listener;

import org.springframework.stereotype.Component;

/**
 * @author YUDI-Corgi
 * @description 自定义消息监听对象
 */
@Component
public class MessageReceiver {

    /**
     * 自定义的接收方法
     */
    public void receive(String msg, String channel) {
        System.out.println("通道：" + channel + "，消息：" + msg);
    }

}
