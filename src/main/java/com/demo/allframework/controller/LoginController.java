package com.demo.allframework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 登录Controller
 * @author YUDI
 * @date 2020/11/22 17:45
 */
@Controller
@RequestMapping("/sys")
public class LoginController {

    @GetMapping("")
    public String login(){
        return "login";
    }

    @GetMapping("/loginSuccess")
    @ResponseBody
    public String loginSuccess(){
        return "登录成功后跳转到这里";
    }

    /**
     * 测试权限接口一
     * @return
     */
    @GetMapping("/t/t1")
    @ResponseBody
    public String t1(){
        return "访问资源 t1";
    }

    /**
     * 测试权限接口二
     * @return
     */
    @GetMapping("/t/t2")
    @ResponseBody
    public String t2(){
        return "访问资源 t2";
    }

}
