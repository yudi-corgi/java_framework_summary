package com.demo.allframework.es.controller;

import com.demo.allframework.es.entity.UserDoc;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedHistogram;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.clients.elasticsearch7.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author YUDI-Corgi
 * @description 聚集查询（也就是聚合、分组等操作）
 */
@RestController
@RequestMapping("agg")
public class AggregationController {

    @Resource
    private ElasticsearchRestTemplate esTemplate;

    // region 度量聚集
    @GetMapping("/statsAgg")
    public void statsAgg() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        // 后置过滤器，不影响聚集操作
        // nsq.withFilter(QueryBuilders.rangeQuery("age").gt(25));
        // 前置过滤器，影响聚集操作
        nsq.withQuery(QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("age").gt(25)));
        // 构建聚集
        StatsAggregationBuilder statistics = AggregationBuilders.stats("statistics").field("age");
        nsq.withAggregations(statistics);
        SearchHits<UserDoc> search = esTemplate.search(nsq.build(), UserDoc.class);
        // 聚集容器
        ElasticsearchAggregations aggContainer = (ElasticsearchAggregations) search.getAggregations();
        assert aggContainer != null;
        // 获取查询结果中聚集信息
        Aggregations aggregations = aggContainer.aggregations();
        // 通过聚集名称获取
        ParsedStats stats = aggregations.get("statistics");
        System.out.println("查询命中的文档数：" + search.getTotalHits());
        System.out.println("age 最大：" + stats.getMax());
        System.out.println("age 最小：" + stats.getMin());
        System.out.println("age 平均：" + stats.getAvg());
        System.out.println("age 总和：" + stats.getSum());
        System.out.println("聚集计算的文档数：" + stats.getCount());
    }

    @GetMapping("/approximateStatistics")
    public void approximateStatistics() {
        NativeSearchQueryBuilder nsq1 = new NativeSearchQueryBuilder();
        // 分别统计 50%、75%、99% 的文档 age 字段值在多少以下
        PercentilesAggregationBuilder agePercentiles = AggregationBuilders.percentiles("age_percentage").field("age").percentiles(50, 75, 99);
        // 统计 age 字段值的基数
        CardinalityAggregationBuilder ageCardinality = AggregationBuilders.cardinality("age_cardinality").field("age");
        nsq1.withAggregations(agePercentiles, ageCardinality);
        SearchHits<UserDoc> result = esTemplate.search(nsq1.build(), UserDoc.class);
        ElasticsearchAggregations aggContainer = (ElasticsearchAggregations) result.getAggregations();
        assert aggContainer != null;
        Aggregations aggregations = aggContainer.aggregations();

        // 百分比
        ParsedTDigestPercentiles percentiles = aggregations.get("age_percentage");
        System.out.println("50% 的文档年龄为：" + percentiles.percentile(50) + " 以下");
        System.out.println("75% 的文档年龄为：" + percentiles.percentile(75) + " 以下");
        System.out.println("99% 的文档年龄为：" + percentiles.percentile(99) + " 以下");

        // 基数
        ParsedCardinality cardinality = aggregations.get("age_cardinality");
        System.out.println("age 基数：" + cardinality.getValue());
    }

    // endregion

    // region 桶聚集
    @GetMapping("/termsAgg")
    public void termsAgg() {
        NativeSearchQueryBuilder nsq1 = new NativeSearchQueryBuilder();
        // 桶聚集最多 20 个 bucket（即分组后有多条数据，也仅返回 20 条）
        nsq1.withAggregations(AggregationBuilders.terms("demo").field("age").size(20));
        SearchHits<UserDoc> search = esTemplate.search(nsq1.build(), UserDoc.class);
        ElasticsearchAggregations aggregationsContainer = (ElasticsearchAggregations) search.getAggregations();
        assert aggregationsContainer != null;
        Aggregations agg = aggregationsContainer.aggregations();
        // 填入聚集名称获取
        ParsedLongTerms demo = agg.get("demo");
        System.out.println("聚集类型：" + demo.getType());
        for (Terms.Bucket bucket : demo.getBuckets()) {
            System.out.println("年龄 - " + bucket.getKeyAsString() + " : 数量 - " + bucket.getDocCount());
        }

        // 统计指定 age 值在所有文档中的百分占比
        NativeSearchQueryBuilder nsq2 = new NativeSearchQueryBuilder();
        nsq2.withAggregations(AggregationBuilders.terms("age_percent").field("age"));
        SearchHits<UserDoc> result = esTemplate.search(nsq2.build(), UserDoc.class);
        ElasticsearchAggregations container = (ElasticsearchAggregations) result.getAggregations();
        assert container != null;
        Aggregations bucket = container.aggregations();
        ParsedLongTerms agePercent = bucket.get("age_percent");
        agePercent.getBuckets().forEach(b -> {
            if ("25".equals(b.getKeyAsString())) {
                System.out.println("25 岁的文档占总文档数的 " + ((double) b.getDocCount() / (double) result.getTotalHits()) * 100 + "%");
            }
        });
    }

    @GetMapping("/rangeAgg")
    public void rangeAgg() {
        // 范围聚集都是左闭右开，即 [x,y)，若只指定一个（from / to），则表示 [-∞,to)、(from,+∞]
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        nsq.withAggregations(AggregationBuilders.range("age_range").field("age").addRange(20, 40));
        SearchHits<UserDoc> search = esTemplate.search(nsq.build(), UserDoc.class);
        ElasticsearchAggregations aggContainer = (ElasticsearchAggregations) search.getAggregations();
        assert aggContainer != null;
        Aggregations aggregations = aggContainer.aggregations();
        ParsedRange ageRange = aggregations.get("age_range");
        System.out.println("age 在 20-40 的文档数量有：" + ageRange.getBuckets().get(0).getDocCount());

        // 时间范围聚集，指定时间格式以便 ES 解析给定的时间字符串（本质上 ES 会将日期型字符串转出为数值型 long，且用毫秒级的 UNIX 时间表示）
        NativeSearchQueryBuilder nsq2 = new NativeSearchQueryBuilder();
        nsq2.withAggregations(AggregationBuilders.dateRange("date_range").field("updateTime")
                .format("yyyy-MM-dd").addRange("2023-02-16", "2023-02-22"));
        SearchHits<UserDoc> result = esTemplate.search(nsq2.build(), UserDoc.class);
        ElasticsearchAggregations container = (ElasticsearchAggregations) result.getAggregations();
        assert container != null;
        Aggregations agg = container.aggregations();
        ParsedDateRange dateRange = agg.get("date_range");
        System.out.println("2023-02-16 ~ 2023-02-22 的文档数：" + dateRange.getBuckets().get(0).getDocCount());
    }

    @GetMapping("/histogramAgg")
    public void histogramAgg() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        // 年龄间隔为 10 的桶聚集
        nsq.withAggregations(AggregationBuilders.histogram("his_agg").field("age").interval(10));
        SearchHits<UserDoc> result = esTemplate.search(nsq.build(), UserDoc.class);
        ElasticsearchAggregations container = (ElasticsearchAggregations) result.getAggregations();
        assert container != null;
        Aggregations aggregations = container.aggregations();
        ParsedHistogram hisAgg = aggregations.get("his_agg");
        List<? extends Histogram.Bucket> buckets = hisAgg.getBuckets();
        for (int i = 0; i < buckets.size(); i++) {
            String from = buckets.get(i).getKeyAsString();
            String to = i != buckets.size() - 1 ? to = buckets.get(i + 1).getKeyAsString() : "+∞";
            System.out.println("年龄在 " + from + " - " + to + " 的文档数: " + buckets.get(i).getDocCount());
        }

        // 时间间隔为 1 天的桶聚集
        NativeSearchQueryBuilder nsq2 = new NativeSearchQueryBuilder();
        nsq2.withAggregations(AggregationBuilders.dateHistogram("date_his_agg")
                .field("updateTime").calendarInterval(DateHistogramInterval.DAY));
        SearchHits<UserDoc> result2 = esTemplate.search(nsq2.build(), UserDoc.class);
        ElasticsearchAggregations container2 = (ElasticsearchAggregations) result2.getAggregations();
        assert container2 != null;
        Aggregations aggregations2 = container2.aggregations();
        ParsedDateHistogram dateHisAgg = aggregations2.get("date_his_agg");
        for (Histogram.Bucket bucket : dateHisAgg.getBuckets()) {
            System.out.println("时间在 " + bucket.getKeyAsString() + " 的文档数：" + bucket.getDocCount());
        }
    }

    // endregion

    // region 嵌套聚集
    @GetMapping("/nestedAgg")
    public void nestedAgg() {
        NativeSearchQueryBuilder nsq = new NativeSearchQueryBuilder();
        // 根据 age 桶聚集，并查出最近一条的更新时间，最多 20 条 bucket
        nsq.withAggregations(AggregationBuilders.terms("age_group").field("age")
                .subAggregation(AggregationBuilders.max("doc_update").field("updateTime")).size(20));
        SearchHits<UserDoc> search = esTemplate.search(nsq.build(), UserDoc.class);
        ElasticsearchAggregations aggContainer = (ElasticsearchAggregations) search.getAggregations();
        assert aggContainer != null;
        Aggregations aggregations = aggContainer.aggregations();
        ParsedLongTerms ageGroup = aggregations.get("age_group");
        ageGroup.getBuckets().forEach(b -> {
            Aggregations agg = b.getAggregations();
            ParsedMax docUpdate = agg.get("doc_update");
            System.out.println("age - " + b.getKeyAsString() + " 的文档中最近一条更新时间为：" + docUpdate.getValueAsString());
        });
    }

    // endregion
}
