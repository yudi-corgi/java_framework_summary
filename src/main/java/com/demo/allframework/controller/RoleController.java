package com.demo.allframework.controller;

import com.demo.allframework.entity.Role;
import com.demo.allframework.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author YUDI
 * @date 2020/5/2 17:45
 */
@Controller
@RequestMapping("sysRole")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(){

        Role role = new Role();
        role.setRoleName("管理员");
        role.setRoleKey("admin");
        role.setRoleSort(1);
        role.setStatus("0");
        roleService.save(role);
        System.out.println(role.getRoleId());
        return "插入成功";
    }

}
