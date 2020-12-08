package com.demo.allframework.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * 令牌持久化配置
 * @author YUDI
 * @date 2020/12/8 22:56
 */
@Configuration
public class TokenStoreConfig {

    @Bean
    public TokenStore tokenStore(){
        // 使用内存存储令牌
        return new InMemoryTokenStore();
    }

}
