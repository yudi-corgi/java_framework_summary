package com.demo.allframework.rocketmq.rmqboot;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author YUDI-Corgi
 * @Description 消息内容实体
 */
@Data
public class RmqMessage<T> implements Serializable {
    private T content;
    private String msgKey;
    private String topic;
    private String group;
    private String tag;
    /**
     * 延迟等级
     */
    private Integer delayLevel;
}
