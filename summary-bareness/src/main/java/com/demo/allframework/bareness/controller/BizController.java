package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.entity.SelectRequest;
import com.demo.allframework.bareness.entity.TestDTO;
import com.demo.allframework.bareness.service.IBizService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 测试控制层
 */
@RestController
@RequestMapping("/biz")
public class BizController {

    @Resource
    private IBizService bizService;

    @GetMapping(value = "get")
    public TestDTO read(SelectRequest request) {
        System.out.println("Select data.");
        return bizService.select(request.getId());
    }

    @PostMapping("post")
    public Long create(@RequestBody TestDTO request) {
        System.out.println("Create data.");
        return bizService.create(request);
    }

    @DeleteMapping("delete")
    public void delete() {
        System.out.println("Delete data.");
    }

    @PutMapping("put")
    public void update() {
        System.out.println("Update data.");
    }

}
