package com.demo.allframework.shardingsphere.controller;

import com.demo.allframework.shardingsphere.entity.User;
import com.demo.allframework.shardingsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CDY
 * @date 2021/10/31
 * @description
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("get")
    public void get(){
        List<User> list = userService.list();
        list.forEach(u -> System.out.println(u.toString()));
    }

    @GetMapping("save")
    public void save(){
        User user = new User();
        user.setUsername("test");
        user.setAge(18);
        user.setBirthday(LocalDateTime.now());
        userService.save(user);
    }

    @GetMapping("update")
    public void update(User user){

    }

    @GetMapping("delete")
    public void delete(Integer id){

    }

}
