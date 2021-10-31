package com.demo.allframework.shardingsphere.job;

import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 作业 ZK 注册中心
 */
public class RegistryCenter {

    /**
     * 创建注册中心（ZK）并初始化
     * @return
     */
    public static CoordinatorRegistryCenter create() {
        ZookeeperRegistryCenter registryCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:2181", "elastic"));
        registryCenter.init();
        return registryCenter;
    }

}
