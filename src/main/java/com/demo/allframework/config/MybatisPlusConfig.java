package com.demo.allframework.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus 配置类
 * @author YUDI
 * @date 2020/11/21 19:06
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * Mybatis-Plus 分页插件配置
     * @return
     */
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(5L);
        return paginationInnerInterceptor;
    }

}