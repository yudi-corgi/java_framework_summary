package com.demo.allframework.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 使用 Spring 自身的定时任务调度
 * @author YUDI
 * @date 2020/5/22 14:28
 */
@Configuration
@EnableScheduling
public class TaskSchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

    }

    @Bean
    public Executor taskScheduler(){
        // 创建线程池，线程数 100
        return Executors.newScheduledThreadPool(100);
    }

}
