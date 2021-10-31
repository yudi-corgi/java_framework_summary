package com.demo.allframework.shardingsphere.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;

import java.util.List;

/**
 * @author CDY
 * @date 2021/10/31
 * @description SpringBoot 方式实现 DataflowJob
 */
public class SpringBootJob implements DataflowJob<String> {

    @Override
    public List<String> fetchData(ShardingContext shardingContext) {
        System.out.println("SpringBoot 方式进入作业");
        return null;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<String> data) {

    }
}
