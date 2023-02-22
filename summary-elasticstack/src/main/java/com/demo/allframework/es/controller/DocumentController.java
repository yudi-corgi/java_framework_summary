package com.demo.allframework.es.controller;

import com.demo.allframework.es.entity.UserDoc;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
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
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        // 当 includes / excludes 指定了相同字段，后者优先级较高，即排除字段
        // 但是若两者指定了不同字段，excludes 就没有了效果，因为 includes 语义就表示除了指定的字段，其它字段都不要查询
        FetchSourceFilterBuilder source = new FetchSourceFilterBuilder();
        // 排除指定字段
        source.withExcludes("age");
        // 查询指定字段
        source.withIncludes("name");
        nsq.withSourceFilter(source.build());
        SearchHits<UserDoc> all = esTemplate.search(nsq.withQuery(QueryBuilders.matchAllQuery()).build(), UserDoc.class);
        return all.stream().map(SearchHit::getContent).collect(Collectors.toList());
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
        for (int i = 20; i < 40; i++) {
            UserDoc doc = new UserDoc();
            doc.setId(i + "");
            doc.setName("Hello" + (i + 100));
            doc.setAge(random.nextInt(50));
            doc.setGender(random.nextInt(1));
            doc.setMobile("i" + 2 + i + 1314 + 9211);
            doc.setIdCard(i + 3000 + "");
            doc.setAddress("地址不详" + i);
            doc.setHobby(new String[]{ "钢琴", "古筝", "萧" });
            doc.setSingle(i - 30 > 0);
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

    @PostMapping("/updateByQuery")
    public void updateByQuery() {
        // 指定查询条件，age > 25 的文档才会被更新
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        nsq.withQuery(QueryBuilders.rangeQuery("age").gt(25));
        UpdateQuery.Builder update = UpdateQuery.builder(nsq.build());

        // 官方示例：https://github.com/spring-projects/spring-data-elasticsearch/blob/4.2.x/src/test/java/org/springframework/data/elasticsearch/core/ElasticsearchTemplateTests.java
        // ScriptType.STORED 则会使用集群中已存储的脚本，通过名称指定要使用的脚本即可
        // update.withScriptType(ScriptType.INLINE).withScriptName("name").build();
        // INLINE 方式需要指定脚本，脚本会实时编译然后缓存起来
        update.withScriptType(ScriptType.INLINE)
                // 当文档 version 冲突时是否中止操作
                .withAbortOnVersionConflict(true)
                // 脚本语言，其它可选值：expression（Lucene 脚本语言）、mustache（搜索模板使用）
                .withLang("painless")
                // 脚本，不带参数，直接指定 k=v
                // .withScript("ctx._source.name='按条件更新名称'");
                // 脚本，带参数
                .withScript("ctx._source['name']= params['afterName']")
                // 指定参数变量，需要与脚本中的参数名对应
                .withParams(Collections.singletonMap("afterName", "什么鬼"));
        ByQueryResponse res = esTemplate.updateByQuery(update.build(), IndexCoordinates.of("user_doc"));
        System.out.println(res);
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
