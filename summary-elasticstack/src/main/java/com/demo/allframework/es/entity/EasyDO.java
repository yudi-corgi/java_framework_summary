package com.demo.allframework.es.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author YUDI-Corgi
 * @description 文档对象，简单测试
 */
@Document(indexName = "demo")
@Data
public class EasyDO {

    private String name;

    private Integer quantity;

    private LocalDate time;

    private Boolean status;

    private BigDecimal price;

    private Double quality;

    private List<String> alias;

    private Map<String, String> score;

    public static void main(String[] args) {
    }

}
