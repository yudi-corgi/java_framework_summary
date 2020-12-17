package com.demo.allframework.zuul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 令牌持久化配置
 * @author YUDI
 * @date 2020/12/8 22:56
 */
@Configuration
public class TokenStoreConfig {

    // 密钥
    private final String SECRET = "uaa123";

    @Bean
    public TokenStore tokenStore(){
        // 使用内存存储令牌
        // return new InMemoryTokenStore();
        // 基于 jwt 的 token 存储方案
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * JwtAccessTokenConverter 用于帮助程序在 JWT 编码的令牌值和 OAuth 身份验证信息之间进行转换
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //对称秘钥，资源服务器使用该秘钥来验证
        converter.setSigningKey(SECRET);
        return converter;
    }

}
