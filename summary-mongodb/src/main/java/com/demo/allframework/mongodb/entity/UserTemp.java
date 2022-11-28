package com.demo.allframework.mongodb.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

/**
 * @author YUDI-Corgi
 * @description
 */
@Data
public class UserTemp {

    /**
     * 标识 _id 字段所映射属性
     */
    @MongoId
    private String id;
    private String name;
    private Integer age;
    private Date birth;

}
