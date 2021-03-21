package com.demo.allframework.uaa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CDY
 * @date 2021/3/21
 * @description
 */
@Controller
public class LoginController {

    @GetMapping("/toLogin")
    public String login(){
        return "login";
    }

    @GetMapping("/index")
    public String index(){
        return "success";
    }

    @GetMapping("/test")
    public String test(){
        return "自动登录测试";
    }
}
