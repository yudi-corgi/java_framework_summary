package com.demo.allframework.shardingsphere.job;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.http.props.HttpJobProperties;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.script.props.ScriptJobProperties;

import java.nio.file.Paths;

/**
 * @author CDY
 * @date 2021/10/31
 * @description Script | HTTP 作业
 */
public class OkScriptOrHttpJob {

    /**
     * Script 作业配置
     * @return JobConfiguration
     */
    public static JobConfiguration scriptJobConfiguration(){
        // 创建作业配置对象，设置作业名称、分片数、分片参数、cron
        return JobConfiguration.newBuilder("scriptJobFive", 3)
                // .shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou")
                // 失效转移、作业异常处理策略
                .failover(true).jobErrorHandlerType("LOG")
                .setProperty(ScriptJobProperties.SCRIPT_KEY, buildScript())
                .build();
    }

    /**
     * Http 作业配置（官方示例）
     * @return JobConfiguration
     */
    public static JobConfiguration httpJobConfiguration(){
        return JobConfiguration.newBuilder("httpJob",1)
                // 配置 http 参数，具体看HttpJobProperties
                .setProperty(HttpJobProperties.URI_KEY, "http://xxx.com/execute")
                .setProperty(HttpJobProperties.METHOD_KEY, "POST")
                .setProperty(HttpJobProperties.DATA_KEY, "source=ejob")
                .cron("0/5 * * * * ?").shardingItemParameters("0=Beijing").build();
    }

    public static String buildScript(){
        if (System.getProperties().getProperty("os.name").contains("Windows")) {
            return Paths.get(OkScriptOrHttpJob.class.getResource("/elastic.bat").getPath().substring(1)).toString();
        }
        return "";
    }

    public static void main(String[] args) {
        new OneOffJobBootstrap(RegistryCenter.create(), "SCRIPT", scriptJobConfiguration()).execute();
        new OneOffJobBootstrap(RegistryCenter.create(), "HTTP", httpJobConfiguration()).execute();
    }

}
