package com.demo.allframework.uaa.entity;

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

    private static final long serialVersionUID = 285262401041674033L;
    private Long roleId;
    private String roleName;
    private String roleKey;
    private int roleSort;
    private String status;


}
