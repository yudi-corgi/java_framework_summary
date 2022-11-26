package com.demo.allframework.mongodb.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUDI-Corgi
 * @description
 */
@RestController
@RequiredArgsConstructor
public class MongoController {

    private final MongoTemplate mongoTemplate;

    @GetMapping("/getAll")
    public void getAll() {
        List<JSONObject> all = mongoTemplate.findAll(JSONObject.class, "users");
        all.forEach(System.out::println);
    }

}
