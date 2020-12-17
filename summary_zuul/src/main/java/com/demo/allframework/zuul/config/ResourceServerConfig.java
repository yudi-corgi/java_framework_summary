package com.demo.allframework.zuul.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 网关资源服务配置
 * 目前只配置 uaa 和 service 两个服务，若有其它，则需新建内部类配置
 * @author YUDI
 * @date 2020/12/16 1:01
 */
@Configuration
public class ResourceServerConfig {

    // 资源ID，对应客户端详情服务里的客户端信息
    public static final String RESOURCE_ID = "res1";
    @Autowired
    private TokenStore tokenStore;

    /**
     * uaa 资源服务配置
     */
    @Configuration
    @EnableResourceServer
    public class UaaResourceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // uaa 服务请求全部无需鉴权
            http.authorizeRequests().antMatchers("/uaa/**").permitAll();
        }
    }

    /**
     * service 资源服务配置
     */
    @Configuration
    @EnableResourceServer
    public class ServiceResourceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // 服务请求必须有 ADMIN 角色权限
            http.authorizeRequests().antMatchers("/sys/**")
                    .access("#oauth2.hasScope('ROLE_ADMIN')");
        }
    }


}
