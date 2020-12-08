package com.demo.allframework.uaa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限信息实体
 * @author YUDI
 * @date 2020/11/23 23:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysPermission extends BaseEntity{

    private static final long serialVersionUID = -2810284494423419700L;
    private Long permId;
    private String permCode;
    private String permDesc;
    private String url;
    private int orderNum;

}
