package com.demo.allframework.controller;

import com.demo.allframework.service.SysPermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限Controller
 * @author YUDI
 * @date 2020/11/23 23:23
 */
@RestController
@RequestMapping("/perm")
public class SysPermController {

    @Autowired
    private SysPermService permService;

}
