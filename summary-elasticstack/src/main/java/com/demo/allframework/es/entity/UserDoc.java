package com.demo.allframework.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author YUDI-Corgi
 * @Description 用户实体（文档映射对象）
 */
@Setting(shards = 1, replicas = 1)
@Document(indexName = "user_doc", createIndex = true, writeTypeHint = WriteTypeHint.TRUE, dynamic = Dynamic.TRUE)
@TypeAlias("userDocs") // 设置类型别名，在 ES 中会以 _class 字段展示，不是别名则 _class 为全限定名
@Data
public class UserDoc {

    @Id
    private String id;
    @Field(name = "name", type = FieldType.Text)
    private String name;
    @Field(name = "age", type = FieldType.Integer)
    private Integer age;
    @Field(name = "gender", type = FieldType.Integer)
    private Integer gender;
    @Field(name = "mobile", type = FieldType.Keyword)
    private String mobile;
    @Field(name = "idCard", type = FieldType.Keyword)
    private String idCard;
    @Field(name = "address", type = FieldType.Text)
    private String address;
    @Field(name = "hobby", type = FieldType.Text)
    private String[] hobby;
    @Field(name = "single", type = FieldType.Boolean)
    private Boolean single;
    @Field(name = "createTime", type = FieldType.Date, format = {}, pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createTime;
    @Field(name = "updateTime", type = FieldType.Date)
    private LocalDateTime updateTime;
    /**
     * Key 需要为字符串才会被 ES 识别
     */
    @Field(name = "otherFieldMap")
    private Map<String, Object> otherFieldMap;
}