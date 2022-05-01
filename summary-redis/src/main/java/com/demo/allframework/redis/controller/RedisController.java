package com.demo.allframework.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author YUDI-Corgi
 * @description
 */
@Slf4j
@RestController
public class RedisController {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @PostMapping("/set")
    public void set(@RequestParam("key") String key, @RequestParam("value") String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @GetMapping("/get/{key}")
    public Object get(@PathVariable("key") String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
