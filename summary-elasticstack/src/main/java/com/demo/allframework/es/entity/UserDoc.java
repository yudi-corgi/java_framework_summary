package com.demo.allframework.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;
import org.springframework.data.elasticsearch.core.Range;

import java.util.Date;
import java.util.Map;

/**
 * @Author YUDI-Corgi
 * @Description 用户实体（文档映射对象）
 */
//@Setting(shards = 5, replicas = 1) 设置主分片、副本分片数量
// 禁用类型别名
@Document(indexName = "first-index", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
@TypeAlias("user") // 设置类型别名，在 ES 中会以 _class 字段展示
@Data
public class UserDoc {

    @Id
    private String id;
    @Field(name = "name", type = FieldType.Text)
    private String name;
    @Field(name = "age", type = FieldType.Integer_Range)
    private Range<Integer> age;
    @Field(name = "gender", type = FieldType.Integer)
    private Integer gender;
    @Field(name = "mobile", type = FieldType.Keyword)
    private String mobile;
    @Field(name = "idCard", type = FieldType.Keyword)
    private String idCard;
    @Field(name = "address", type = FieldType.Text)
    private String address;
    @Field(name = "hobby", type = FieldType.Text)
    private String hobby;
    @Field(name = "single", type = FieldType.Boolean)
    private Boolean single;
    //@Field(name = "createTime", type = FieldType.Date, format = {}, pattern = "yyyy.MM.dd HH.mm.ss")
    @Field(name = "createTime", type = FieldType.Date)
    private Date createTime;
    //@Field(name = "updateTime", type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(name = "createTime", type = FieldType.Date)
    private Date updateTime;
    /**
     * Key 需要为字符串才会被 ES 识别
     */
    @Field(name = "otherFieldMap")
    private Map<String, Object> otherFieldMap;
}