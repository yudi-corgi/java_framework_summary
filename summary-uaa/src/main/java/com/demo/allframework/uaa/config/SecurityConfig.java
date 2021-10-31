package com.demo.allframework.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * SpringSecurity 权限配置
 * @author YUDI
 * @date 2020/11/22 17:44
 */
@Configuration
// 该注解表示开启基于注解的安全拦截，如 prePostEnabled:{@preAuthorize,@postAuthorize} securedEnabled:{@Secured}
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;
    @Autowired
    @Qualifier("securityUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        // 启动时自动创建数据库表
        // jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    /**
     * 注册认证管理器实例对象，授权服务设置密码授权类型时使用
     * @return
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 密码编码器，用于密码校验，指明密码加密方式
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // 使用明文比对，已弃用
        // return NoOpPasswordEncoder.getInstance();
        // BCrypt
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全拦截机制配置核心方法
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/sys/t/t1").hasAnyAuthority("admin")
                .antMatchers("/login*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/toLogin").permitAll()
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index").permitAll()
                .and().rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60)
                .userDetailsService(userDetailsService);
    }

}
