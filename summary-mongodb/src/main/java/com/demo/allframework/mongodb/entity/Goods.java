package com.demo.allframework.mongodb.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

/**
 * @author YUDI-Corgi
 * @description
 */
@Data
@Document("goods")
public class Goods {

    @MongoId
    private String id;

    private String name;
    private Date produce;
    private Integer time;

}
