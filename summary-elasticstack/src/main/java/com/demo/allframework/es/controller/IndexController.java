package com.demo.allframework.es.controller;

import com.demo.allframework.es.entity.UserDoc;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author YUDI-Corgi
 * @Description 索引操作
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    private ElasticsearchRestTemplate esRestTemplate;

    @GetMapping
    public void get() {
        List<IndexInformation> information = esRestTemplate.indexOps(UserDoc.class).getInformation();
        information.forEach(i -> {
            System.out.println("索引名称：" + i.getName());
            System.out.println("索引设置（分片、副本等）：" + i.getSettings());
            System.out.println("索引映射（文档）：" + i.getMapping());
            System.out.println("索引别名：" + i.getAliases());
        });
    }

    @GetMapping("/all")
    public void getAll() {
        List<IndexInformation> information = esRestTemplate.indexOps(IndexCoordinates.of("*")).getInformation();
        System.out.println(information.size());
    }

    @GetMapping("/exists")
    public boolean exists() {
        return esRestTemplate.indexOps(UserDoc.class).exists();
    }

    @PutMapping
    public boolean create() {
        if (esRestTemplate.indexOps(UserDoc.class).create()) {
            return esRestTemplate.indexOps(UserDoc.class).putMapping();
        }
        return false;
    }

    @DeleteMapping
    public boolean delete() {
        return esRestTemplate.indexOps(UserDoc.class).delete();
    }

}
