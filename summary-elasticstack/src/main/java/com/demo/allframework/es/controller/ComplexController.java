package com.demo.allframework.es.controller;

import com.demo.allframework.es.entity.UserDoc;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUDI-Corgi
 * @description 文档复杂查询
 */
@RestController
@RequestMapping("/complex")
public class ComplexController {

    @Resource
    private ElasticsearchRestTemplate esTemplate;

    @GetMapping("/bool")
    public List<UserDoc> bool() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        BoolQueryBuilder bqq = QueryBuilders.boolQuery();
        /*
          嵌套 Bool 查询条件，bqq 的查询条件会内套在 nested 中
          同个查询对象 must 调用效果如下：
          match: [
            "match": { "age": 14},
            "bool": {
               "should": { "name": "hello107" }
            }
          ]
        */
        // bqq.must(QueryBuilders.matchQuery("age", "14"));
        bqq.should(QueryBuilders.matchQuery("name", "hello107"));
        BoolQueryBuilder nested = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("age", "14")).must(bqq);
        SearchHits<UserDoc> all = esTemplate.search(nsq.withQuery(nested).build(), UserDoc.class);
        return all.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @GetMapping("/page")
    public List<UserDoc> page() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        BoolQueryBuilder bqq = QueryBuilders.boolQuery();
        bqq.should(QueryBuilders.matchQuery("name", "hello102"));
        bqq.should(QueryBuilders.rangeQuery("age").gt(25));
        PageRequest pr = PageRequest.of(0, 2);
        SearchHits<UserDoc> all = esTemplate.search(nsq.withQuery(bqq).withPageable(pr).build(), UserDoc.class);
        return all.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @GetMapping("/sort")
    public List<UserDoc> sort() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        FieldSortBuilder fsb = SortBuilders.fieldSort("age").order(SortOrder.ASC);
        // 也可直接通过分页对象指定排序条件
        // PageRequest pr = PageRequest.of(0, 2, Sort.by("age").descending());
        NativeSearchQuery query = nsq.withQuery(QueryBuilders.matchAllQuery()).withSorts(fsb).build();
        SearchHits<UserDoc> page = esTemplate.search(query, UserDoc.class);
        return page.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @GetMapping("/collapse")
    public List<UserDoc> collapse() {
        // collapse：折叠，也就是去重
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        FieldSortBuilder fsb = SortBuilders.fieldSort("age").order(SortOrder.DESC);
        NativeSearchQuery query = nsq.withQuery(QueryBuilders.matchAllQuery())
                .withCollapseField("age").withSorts(fsb).build();
        SearchHits<UserDoc> collapse = esTemplate.search(query, UserDoc.class);
        return collapse.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}
