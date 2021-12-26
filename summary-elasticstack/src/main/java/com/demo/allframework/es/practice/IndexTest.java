package com.demo.allframework.es.practice;

import cn.hutool.core.lang.UUID;
import com.demo.allframework.es.entity.UserDoc;
import lombok.SneakyThrows;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.core.DocumentOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.Range;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * @Author YUDI-Corgi
 * @Description 索引操作类
 */
@RestController
public class IndexTest {

    @Resource
    private RestHighLevelClient highLevelClient;
    @Resource
    private ElasticsearchOperations esOperations;
    @Resource
    private DocumentOperations docOperations;
    @Resource
    private ElasticsearchRestTemplate esRestTemplate;

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

    @PostMapping("/user")
    public String save(@RequestBody UserDoc userDoc) {
        userDoc.setAge(Range.open(1,18));
        userDoc.setCreateTime(new Date());
        userDoc.setUpdateTime(new Date());
        IndexQuery iq = new IndexQueryBuilder()
                .withId(userDoc.getId())
                .withObject(userDoc)
                .withSource(userDoc.toString())
                .build();
        UserDoc save = docOperations.save(userDoc);
        System.out.println(save);
        return "";
        //return esRestTemplate.doIndex(iq, IndexCoordinates.of("first-index"));
    }

    @GetMapping("/user/{id}")
    public UserDoc findByDocId(@PathVariable("id") String id){
        UserDoc userDoc = esRestTemplate.get(id, UserDoc.class, IndexCoordinates.of("first-index"));
        if (Objects.isNull(userDoc)) {
            throw new RuntimeException("用户文档不存在，ID：" + id);
        }
        return userDoc;
    }
}
