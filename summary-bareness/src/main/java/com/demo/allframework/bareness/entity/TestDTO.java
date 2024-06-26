package com.demo.allframework.bareness.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试实体
 */
@Data
public class TestDTO {

    private Long id;

    private String text;

    private Integer number;

    private BigDecimal amount;

    private List<String> collection;

}
