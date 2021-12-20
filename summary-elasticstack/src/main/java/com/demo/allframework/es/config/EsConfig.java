package com.demo.allframework.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Configuration;

/**
 * @author CDY
 * @date 2021/7/25
 * @description ES 配置类（官方）
 */
@Configuration
public class EsConfig {

    //@Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200)));
    }
}
