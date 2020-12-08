package com.demo.allframework.uaa.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础信息实体类
 * @author YUDI
 * @date 2020/11/23 23:16
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -7232530414588052444L;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;
}
