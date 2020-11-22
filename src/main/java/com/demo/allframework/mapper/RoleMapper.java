package com.demo.allframework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.allframework.entity.Role;

/**
 * @author YUDI
 * @date 2020/5/2 17:39
 */
public interface RoleMapper extends BaseMapper<Role> {

    int insert(Role role);


}
