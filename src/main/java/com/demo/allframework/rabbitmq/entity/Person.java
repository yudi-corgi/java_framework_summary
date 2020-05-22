package com.demo.allframework.rabbitmq.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author YUDI
 * @date 2020/5/21 9:40
 */
@Data
public class Person implements Serializable {

    private static final long serialVersionUID = -6485126139959606972L;
    private String id;
    private String name;
    /** 存储消息发送的唯一标识 */
    private String messageId;

}
