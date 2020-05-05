package com.demo.allframework.role.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author YUDI
 * @date 2020/5/2 17:37
 */
@Data
public class Role {

    private Long roleId;
    private String roleName;
    private String roleKey;
    private int roleSort;
    private String status;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;


}
