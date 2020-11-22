package com.demo.allframework.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.allframework.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * @author YUDI
 * @date 2020/5/2 17:39
 */
public interface RoleMapper extends BaseMapper<Role> {

    int insert(Role role);


}
