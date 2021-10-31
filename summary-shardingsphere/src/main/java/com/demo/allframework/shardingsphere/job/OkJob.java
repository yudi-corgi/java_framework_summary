package com.demo.allframework.shardingsphere.job;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

/**
 * @author CDY
 * @date 2021/10/31
 * @description Simple 作业
 */
public class OkJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        System.out.println("任务执行...");
        System.out.println("任务ID：" + context.getTaskId());
        System.out.println("作业名称：" + context.getJobName());
        System.out.println("作业参数：" + context.getJobParameter());
        System.out.println("分片项：" + context.getShardingItem());
        System.out.println("分片参数：" + context.getShardingParameter());
        System.out.println("分片总数：" + context.getShardingTotalCount());
        switch (context.getShardingItem()){
            case 1: System.out.println("分片一作业调度.."); break;
            case 2: System.out.println("分片二作业调度.."); break;
            case 3: System.out.println("分片三作业调度.."); break;
            default: System.out.println("呵呵"); break;
        }
    }

    /**
     * 定时调度作业配置
     * @return JobConfiguration
     */
    public static JobConfiguration scheduleJobConfiguration(){
        // 创建作业配置对象，设置作业名称、分片数、分片参数、cron
        return JobConfiguration.newBuilder("simpleJob", 3)
                .shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou").cron("0/30 * * * * ?")
                // 失效转移、作业分片策略
                .failover(true).jobShardingStrategyType("AVG_ALLOCATION")
                // 作业异常处理策略
                .jobErrorHandlerType("LOG").build();
    }

    /**
     * 一次性调度作业配置
     * @return
     */
    public static JobConfiguration oneOffJobConfiguration(){
        // 创建作业配置对象，设置作业名称、分片数、分片参数
        return JobConfiguration.newBuilder("simpleJob", 3)
                .shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou")
                // 失效转移、作业异常处理策略（LOG：记录日志但不中断执行，THROW：抛出异常中断执行，IGNORE：忽略异常但不中断执行）
                .failover(true).jobErrorHandlerType("LOG").build();
    }

    public static void main(String[] args) {
        // 定时调度
        new ScheduleJobBootstrap(RegistryCenter.create(), new OkJob(), scheduleJobConfiguration()).schedule();
        // 一次性调度
        new OneOffJobBootstrap(RegistryCenter.create(), new OkJob(), oneOffJobConfiguration()).execute();
    }

}
