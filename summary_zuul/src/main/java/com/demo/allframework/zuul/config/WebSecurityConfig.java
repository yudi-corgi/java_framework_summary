package com.demo.allframework.zuul.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 请求安全配置(Security)
 * @author YUDI
 * @date 2020/12/18 1:46
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 此处虽不校验权限(/**)，但在资源服务配置中已设置 /sys/** 的请求路径需要 ROLE_ADMIN 权限
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().csrf().disable();
    }
}
