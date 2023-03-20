package com.demo.allframework.es.controller;

import com.demo.allframework.es.entity.UserDoc;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
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
          must: [
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

    @GetMapping("/geoDistance")
    public List<UserDoc> geoDistance() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 指定 Geo 字段名
        GeoDistanceQueryBuilder location = new GeoDistanceQueryBuilder("geoCity");
        // 可填 PLANE：按平面计算距离，速度快，但在两极会不准确
        // ARC：弧形计算，将地球当作球面计算距离，较为准确
        location.geoDistance(GeoDistance.ARC);
        // 指定当前位置
        location.point(23.111, 112.123);
        // 以指定位置为中心的圆的半径，落在该圆圈内的点都是匹配的，此处指定 3km
        location.distance("3", DistanceUnit.KILOMETERS);
        // location.distance("12km"); 或者直接写距离+单位
        boolQuery.filter(location);
        // 构建地理排序
        GeoDistanceSortBuilder sort = new GeoDistanceSortBuilder("location", 23.111, 112.123);
        sort.unit(DistanceUnit.KILOMETERS).order(SortOrder.ASC);

        NativeSearchQuery build = nsq.withQuery(boolQuery).withSorts(sort).build();
        SearchHits<UserDoc> geo = esTemplate.search(build, UserDoc.class);
        return geo.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    /**
     * 计算两点距离
     * @param latitudeA  A 地点纬度
     * @param longitudeA A 地点经度
     * @param latitudeB  B 地点纬度
     * @param longitudeB B 地点经度
     * @return 两点距离
     */
    public static double getDistance(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        // 经度
        double lat1 = Math.toRadians(longitudeA);
        double lat2 = Math.toRadians(longitudeB);
        // 纬度
        double lng1 = Math.toRadians(latitudeA);
        double lng2 = Math.toRadians(latitudeB);
        // 经度之差
        double a = lat1 - lat2;
        // 纬度之差
        double b = lng1 - lng2;
        // 计算两点距离的公式
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        // 弧长乘地球半径, 返回单位: 千米
        s =  s * 6356.9088;
        return s;
    }

}
