package com.demo.allframework.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring cache 相关配置
 * @author YUDI
 * @date 2020/8/22 14:51
 */
@Configuration
@EnableCaching //启用缓存功能
public class CacheConfig {

    /**
     * 创建名称为 cache1 的缓存管理器，内部结构实为 concurrentHashMap
     * @return
     */
    @Bean
    public CacheManager cacheManager(){
        return new ConcurrentMapCacheManager("cache1");
    }

}
