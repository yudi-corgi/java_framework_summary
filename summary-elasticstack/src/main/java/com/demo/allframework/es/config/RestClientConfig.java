package com.demo.allframework.es.config;

import com.demo.allframework.es.entity.UserDoc;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author YUDI-Corgi
 * @Description SpringDataES 客户端配置
 */
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        // 创建客户端配置，通过该配置类可获取客户端的配置信息
        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                //.usingSsl()
                .withConnectTimeout(Duration.ofSeconds(5))              // 连接超时时间
                .withSocketTimeout(Duration.ofSeconds(3))               // 会话超时时间
                .withBasicAuth("elastic", "esroot")   // 访问账户密码
                // 设置请求头
                .withHeaders(() -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    headers.add("Jwt-Authorization", "Cori-123");
                    return headers;
                })
                // 通过回调函数为非响应式客户端设置拦截器
                .withClientConfigurer(RestClients.RestClientConfigurationCallback.from((clientBuilder)->{
                    clientBuilder.addInterceptorFirst((HttpRequestInterceptor) (httpRequest, httpContext) -> System.out.println("请求拦截器"));
                    // ...some setting
                    return clientBuilder;
                }))
                .build();
        RestHighLevelClient rest = RestClients.create(config).rest();
        // 应用配置创建 RestHighLevelClient
        return RestClients.create(config).rest();
    }

    /**
     * 自定义 POJO 字段与 ES 字段的映射转化器
     * @return 自定义转化器
     */
    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(Arrays.asList(new UserDocToMap(), new MapToUser()));
    }

    /**
     * 静态内部类，UserDoc -> Map（写入 ES）
     */
    @WritingConverter
    static class UserDocToMap implements Converter<UserDoc, Map<String, Object>> {
        @Override
        public Map<String, Object> convert(UserDoc source) {
            return null;
        }
    }

    /**
     * 静态内部类，Map -> UserDoc（从 ES 读取）
     */
    @ReadingConverter
    static class MapToUser implements Converter<Map<String, Object>, UserDoc> {
        @Override
        public UserDoc convert(Map<String, Object> source) {
            return null;
        }
    }
}
