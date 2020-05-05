package com.demo.allframework.role.service.impl;

import com.demo.allframework.role.dao.RoleDao;
import com.demo.allframework.role.entity.Role;
import com.demo.allframework.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author YUDI
 * @date 2020/5/2 17:46
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public void insert(Role role) {
        roleDao.insert(role);
    }
}
