package com.demo.allframework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.allframework.dao.RoleMapper;
import com.demo.allframework.entity.Role;
import com.demo.allframework.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @author YUDI
 * @date 2020/5/2 17:46
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
