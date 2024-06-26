package com.demo.allframework.bareness.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试实体
 */
@Data
@Accessors(chain = true)
public class TestDTO {

    private Long id;

    private String text;

    private Integer number;

    private BigDecimal amount;

    private List<String> collection;

}
