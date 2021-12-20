package com.demo.allframework.es.practice;

import cn.hutool.core.lang.UUID;
import lombok.SneakyThrows;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @Author YUDI-Corgi
 * @Description 索引操作类
 */
@RestController
public class IndexTest {

    @Resource
    private RestHighLevelClient highLevelClient;

    @SneakyThrows
    @PostMapping("/index")
    public void create() {
        IndexRequest ir = new IndexRequest("first-index")
                .id(UUID.fastUUID().toString(true))
                .source(Collections.singletonMap("feature", "high-level-rest-client"))
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        IndexResponse index = highLevelClient.index(ir, RequestOptions.DEFAULT);
        System.out.println(index.getIndex());
        System.out.println(index.getVersion());
        System.out.println(index);
    }

}
