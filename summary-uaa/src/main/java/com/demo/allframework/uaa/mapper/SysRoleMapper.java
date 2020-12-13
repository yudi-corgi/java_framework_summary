package com.demo.allframework.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.allframework.uaa.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author YUDI
 * @date 2020/5/2 17:39
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    int insert(SysRole sysRole);


}
