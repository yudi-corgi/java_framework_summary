package com.demo.allframework.es.controller;

import com.demo.allframework.es.entity.UserDoc;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.MultiGetItem;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YUDI-Corgi
 * @description 文档 CRUD 基础操作
 */
@RestController
@RequestMapping("/doc")
public class DocumentController {

    @Resource
    private ElasticsearchRestTemplate esTemplate;

    private final Random random;
    {
        random = new Random();
        random.setSeed(31);
    }

    @GetMapping("/{id}")
    public UserDoc get(@PathVariable("id") String docId) {
        return esTemplate.get(docId, UserDoc.class);
    }

    @GetMapping("/all")
    public List<UserDoc> getAll() {
        List<MultiGetItem<UserDoc>> allUserDocItems = esTemplate.multiGet(new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery()).build(), UserDoc.class, IndexCoordinates.of("user_doc"));
        return allUserDocItems.stream().map(MultiGetItem::getItem).collect(Collectors.toList());
    }

    @PutMapping
    public void create() {
        UserDoc doc = new UserDoc();
        doc.setId(UUID.randomUUID().toString());
        doc.setName("哈哈哈");
        doc.setAge(random.nextInt(30));
        doc.setGender(1);
        doc.setMobile("123456789#");
        doc.setIdCard("9527");
        doc.setAddress("黑盒");
        doc.setHobby(new String[]{ "科技", "媒体", "运动" });
        doc.setSingle(true);
        doc.setCreateTime(LocalDateTime.now());
        doc.setUpdateTime(LocalDateTime.now());

        Map<String, Object> otherData = new HashMap<>();
        otherData.put("school", "MIT");
        otherData.put("cn", "google");
        otherData.put("seniority", 2);
        doc.setOtherFieldMap(otherData);

        esTemplate.save(doc);
    }

    @PutMapping("/batch")
    public void batchCreate() {
        List<UserDoc> userDocs = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            UserDoc doc = new UserDoc();
            doc.setId(i + "");
            doc.setName("Hello" + (i + 100));
            doc.setAge(random.nextInt(50));
            doc.setGender(random.nextInt(1));
            doc.setMobile("i" + 2 + i + 1314 + 9211);
            doc.setIdCard(i + 3000 + "");
            doc.setAddress("地址不详" + i);
            doc.setHobby(new String[]{ "钢琴", "古筝", "萧" });
            doc.setSingle(i - 7 > 0);
            doc.setCreateTime(LocalDateTime.now());
            doc.setUpdateTime(LocalDateTime.now());
            userDocs.add(doc);
        }
        esTemplate.save(userDocs);
    }

    @PostMapping("/{id}")
    public void update(@PathVariable("id") String id) {
        UserDoc doc = new UserDoc();
        doc.setId(id);
        doc.setName("这就是修改后的");
        doc.setAddress("地址明确但我不说...");
        // 文档 ID 已存在会直接覆盖，未赋值的字段会变为 null
        esTemplate.save(doc);
    }

    @PostMapping("/partial/{id}")
    public void partialUpdate(@PathVariable("id") String id) {

        // 构建需要更新的部分文档信息
        Document doc = Document.create();
        doc.put("name", "这是部分修改后的名称");
        doc.put("address", "这是部分修改后的地址");
        doc.put("age", 28);
        UpdateQuery update = UpdateQuery.builder(id).withDocument(doc).build();
        UpdateResponse res = esTemplate.update(update, IndexCoordinates.of("user_doc"));
        if (res.getResult() == UpdateResponse.Result.UPDATED) {
            System.out.println("Partial renewal success.");
        }
    }

    @PostMapping("/batchPartial")
    public void batchPartialUpdate() {
        List<UpdateQuery> updateList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Document doc = Document.create();
            doc.put("name", "修改名称啦" + i);
            doc.put("updateTime", LocalDateTime.now());
            updateList.add(UpdateQuery.builder(i + "").withDocument(doc).build());
        }
        esTemplate.bulkUpdate(updateList, UserDoc.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        System.out.println(esTemplate.delete(id, UserDoc.class));
    }

    @DeleteMapping("/byAge")
    public void deleteByAge() {
        // 删除 age>=28 的文档
        NativeSearchQuery nsqAge = new NativeSearchQueryBuilder().withQuery(QueryBuilders.rangeQuery("age").gte(28)).build();
        ByQueryResponse res = esTemplate.delete(nsqAge, UserDoc.class);
        System.out.println("删除的条数：" + res.getDeleted());
    }

    @DeleteMapping("/all")
    public void deleteAll() {
        NativeSearchQuery nsq = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        ByQueryResponse res = esTemplate.delete(nsq, UserDoc.class);
        System.out.println("删除的条数：" + res.getDeleted());
    }

}
