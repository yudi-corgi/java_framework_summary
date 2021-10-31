package com.demo.allframework.shardingsphere.job;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.apache.shardingsphere.elasticjob.dataflow.props.DataflowJobProperties;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 数据流作业
 */
public class OkDataflowJob implements DataflowJob<String> {

    static Map<String, String> data = new HashMap<>();
    static {
        data.put("a", "1");
        data.put("b", "2");
        data.put("c", "3");
    }

    /**
     * 定时调度作业配置
     *
     * @return JobConfiguration
     */
    public static JobConfiguration scheduleJobConfiguration() {
        // 创建作业配置对象，设置作业名称、分片数、分片参数、cron
        return JobConfiguration.newBuilder("DataflowJobByTrue", 3)
                .shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou").cron("20 * * * * ?")
                // 失效转移、作业异常处理策略
                .failover(true).jobErrorHandlerType("LOG")
                .setProperty(DataflowJobProperties.STREAM_PROCESS_KEY, "true")
                .build();
    }

    @Override
    public List<String> fetchData(ShardingContext context) {
        List<String> list = new ArrayList<>();
        switch (context.getShardingItem()) {
            case 1:
                list.add(data.get("a"));
                break;
            case 2:
                list.add(data.get("b"));
                break;
            case 3:
                list.add(data.get("c"));
                break;
            default:
                System.out.println("呵呵");
                break;
        }
        return list;
    }

    @Override
    public void processData(ShardingContext context, List<String> data) {
        System.out.println("数据流任务执行...");
        System.out.println("任务ID：" + context.getTaskId());
        System.out.println("作业名称：" + context.getJobName());
        System.out.println("作业参数：" + context.getJobParameter());
        System.out.println("分片项：" + context.getShardingItem());
        System.out.println("分片参数：" + context.getShardingParameter());
        System.out.println("分片总数：" + context.getShardingTotalCount());
        data.forEach(System.out::printf);
    }

    public static void main(String[] args) {
        new ScheduleJobBootstrap(RegistryCenter.create(), new OkDataflowJob(), scheduleJobConfiguration()).schedule();
    }

}
