package com.demo.allframework.controller;

import com.demo.allframework.dto.UserDto;
import com.demo.allframework.entity.SysUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录Controller
 * @author YUDI
 * @date 2020/11/22 17:45
 */
@Controller
@RequestMapping("/sys")
public class LoginController {

    /**
     * 获取登录用户名称
     * @return  用户名称
     */
    private String getUsername(){
        String username;
        // 获取当前安全上下文认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if(principal == null){
            return "游客";
        }
        if(principal instanceof UserDetails){
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        }else{
            SysUser user = (SysUser) principal;
            username = user.getName();
        }
        return username;
    }

    @GetMapping("")
    public String login(){
        return "login";
    }

    @GetMapping("/loginSuccess")
    @ResponseBody
    public String loginSuccess(){
        return "欢迎《"+ getUsername() +"》登录！！！";
    }


    /**
     * 测试权限接口一，@PreAuthorize 可以传递 SpEL 表达式
     * @return
     */
    @PreAuthorize("hasAnyAuthority('admin','tourist')")
    @GetMapping("/t/t1")
    @ResponseBody
    public String t1(){
        UserDto principal = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUsername() + ":" + principal.getName() + " 访问资源 t1";
    }

    /**
     * 测试权限接口二
     * @return
     */
    // 表示有 admin 权限才可以访问
    @PreAuthorize("hasAuthority('admin')")
    // @PreAuthorize("isAnonymous()")   // 表示匿名访问，登录了反而不可以，秀!
    // @PostAuthorize("hasAuthority('admin')")  // 方法执行后的权限校验
    @GetMapping("/t/t2")
    @ResponseBody
    public String t2(){
        return getUsername() + " 访问资源 t2";
    }

    /**
     * 测试权限接口三，ROLE_ 前缀必须携带，表示要有 tourist 角色才可访问，大小写识别
     * @return
     */
    @Secured("ROLE_tourist")
    @GetMapping("/t/t3")
    @ResponseBody
    public String t3(){
        return getUsername() + " 访问资源 t3";
    }

    /**
     * 测试权限接口四，IS_AUTHENTICATED_ANONYMOUSLY 表示用户可以匿名访问，无需权限
     * @return
     */
    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    @GetMapping("/t/t4")
    @ResponseBody
    public String t4(){
        return getUsername() + " 访问资源 t4";
    }
}
