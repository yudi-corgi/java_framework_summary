package com.demo.allframework.es.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        // 创建客户端配置，通过该配置类可获取客户端的配置信息
        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo("localhost:9200").build();
        // 应用配置创建 RestHighLevelClient
        return RestClients.create(config).rest();
    }
}
