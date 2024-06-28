package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.entity.User;
import com.demo.allframework.bareness.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/findById")
    public User findById(@RequestParam Long id) {
        return userService.findById(id);
    }

    @PostMapping("/save")
    public Integer save(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/update")
    public Integer update(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/delete/{id}")
    public Integer delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

}
