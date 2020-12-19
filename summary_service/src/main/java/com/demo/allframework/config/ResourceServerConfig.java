package com.demo.allframework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author YUDI
 * @date 2020/12/16 1:01
 */
@Configuration
// 标识资源服务器
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    // 资源ID，对应客户端详情服务里的客户端信息
    public static final String RESOURCE_ID = "res1";
    @Autowired
    private TokenStore tokenStore;

    /**
     * 资源服务配置
     * @param resources
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID)
                // 在资源服务器设置 token 存储策略(JWT)，由资源服务器自己校验令牌
                .tokenStore(tokenStore)
                // 解析令牌的服务，实际调用的还是认证服务器去验证令牌
                // .tokenServices(tokenServices())
                .stateless(true);
    }

    // 资源服务令牌解析服务，调用认证服务器校验 token
    @Bean
    public ResourceServerTokenServices tokenServices(){
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        // 设置检查 token 的端点地址
        tokenServices.setCheckTokenEndpointUrl("http://localhost:9091/uaa/oauth/check_token");
        tokenServices.setClientId("c1");
        tokenServices.setClientSecret("secret");
        return tokenServices;
    }

    /**
     * HTTP 安全配置，类似 SpringSecurity 的安全配置
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").access("#oauth2.hasScope('ROLE_ADMIN')") // 表示请求访问都需含有 ROLE_ADMIN 授权范围
                .and().csrf().disable()
                .sessionManagement()
                // Session 创建策略，STATELESS：不创建且不使用其它方式生成的 session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
