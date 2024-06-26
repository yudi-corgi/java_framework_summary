package com.demo.allframework.bareness.controller;

import org.springframework.web.bind.annotation.*;

/**
 * 测试控制层
 */
@RestController
@RequestMapping("/biz")
public class BizController {

    @GetMapping("")
    public void read() {
        System.out.println("Select data.");
    }

    @PostMapping("")
    public void create() {
        System.out.println("Create data.");
    }

    @DeleteMapping("")
    public void delete() {
        System.out.println("Delete data.");
    }

    @PutMapping("")
    public void update() {
        System.out.println("Update data.");
    }

}
