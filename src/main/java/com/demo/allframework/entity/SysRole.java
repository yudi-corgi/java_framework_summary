package com.demo.allframework.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色信息实体
 * @author YUDI
 * @date 2020/5/2 17:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity{

    private Long roleId;
    private String roleName;
    private String roleKey;
    private int roleSort;
    private String status;


}
