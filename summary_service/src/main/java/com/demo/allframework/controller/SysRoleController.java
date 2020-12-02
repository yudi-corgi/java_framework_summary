package com.demo.allframework.controller;

import com.demo.allframework.entity.SysRole;
import com.demo.allframework.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author YUDI
 * @date 2020/5/2 17:45
 */
@Controller
@RequestMapping("role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(){

        SysRole sysRole = new SysRole();
        sysRole.setRoleName("管理员");
        sysRole.setRoleKey("admin");
        sysRole.setRoleSort(1);
        sysRole.setStatus("0");
        roleService.save(sysRole);
        System.out.println(sysRole.getRoleId());
        return "插入成功";
    }

}
