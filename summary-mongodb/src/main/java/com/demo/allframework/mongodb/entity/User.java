package com.demo.allframework.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

/**
 * @author YUDI-Corgi
 * @description @CompoundIndexes 是容器注解，可使用多个 @CompoundIndex 声明要创建的复合索引，搭配配置属性 auto-index-creation:true 使用
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
@CompoundIndexes({@CompoundIndex(def = "{h1: 1, h2: -1}", name = "h1_h2_IDX"), @CompoundIndex(def = "{h3: 1, h4: 1}", sparse = true)})
public class User {

    /**
     * 复合索引试验字段...
     */
    private String h1, h2;
    private String h3, h4;

    /**
     * 标识 _id 字段所映射属性
     */
    @MongoId
    private String id;

    /**
     * 注解用于文档与实体 字段映射
     */
    @Field("username")
    private String name;

    /**
     * 不使用注解则默认以属性名为 Key
     */
    private Integer age;

    /**
     * 指定字段类型，不指定则根据值判断
     */
    @Field(targetType = FieldType.DATE_TIME)
    private Date birth;

    /**
     * 指定该字段为索引字段并降序
     */
    @Indexed(direction = IndexDirection.DESCENDING)
    private Double money;

    @Version
    private Integer version;

    public User(String h1, String h2, String h3, String h4, String id, String name, Integer age, Date birth, Double money) {
        this.h1 = h1;
        this.h2 = h2;
        this.h3 = h3;
        this.h4 = h4;
        this.id = id;
        this.name = name;
        this.age = age;
        this.birth = birth;
        this.money = money;
    }

    public User(String id, String name, Integer age, Date birth) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.birth = birth;
    }
}
