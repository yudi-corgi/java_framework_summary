package com.demo.allframework.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户信息表(SysUser)实体类
 *
 * @author YUDi
 * @date 2020-04-28
 */
@Data
public class SysUser implements Serializable {
    private static final long serialVersionUID = 770018988602469525L;
    /**
    * 用户ID
    */
    private Long id;
    /**
    * 用户名称
    */
    private String name;
    /**
    * 用户类型
    */
    private String type;
    /**
    * 性别
    */
    private String gender;
    /**
    * 登录账号
    */
    private String loginName;
    /**
    * 密码
    */
    private String password;
    /**
    * 创建者
    */
    private String createBy;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新者
    */
    private String updateBy;
    /**
    * 更新时间
    */
    private Date updateTime;
    /**
    * 备注
    */
    private String remark;

}