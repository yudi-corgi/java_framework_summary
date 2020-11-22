package com.demo.allframework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * SpringSecurity 权限配置
 * @author YUDI
 * @date 2020/11/22 17:44
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 定义用户信息服务（查询用户信息）
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(){
        // 创建 InMemoryUserDetailsManager 对象，构建用户账户信息保存在内存当中
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password(BCrypt.hashpw("admin",BCrypt.gensalt())).authorities("admin").build());
        manager.createUser(User.withUsername("tourist").password(BCrypt.hashpw("123456",BCrypt.gensalt())).authorities("user").build());
        return manager;
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
        // 拦截配置具有优先级，先配置的机制覆盖后配置的
        // 关闭 csrf（跨站请求仿造）保护，是为了表单提交账户信息不被拦截
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/sys/t/t1").hasAuthority("admin")     // 表示拥有 admin 权限才可以访问
                .antMatchers("/sys/t/t2").hasAuthority("user")     // 表示拥有 user 权限才可以访问
                .antMatchers("/sys/t/**").authenticated()     // 表示 /sys/** 的请求都需要认证
                .anyRequest().permitAll()       // 除了上面配置的请求路径，其余请求通过，
                .and()
                .formLogin()    // 允许表单登录
                .loginPage("/sys")     // 设置登录地址
                .loginProcessingUrl("/sys/login")   // 设置登录处理的接口地址，Security 默认是 */login*
                .defaultSuccessUrl("/sys/loginSuccess");   // 登录后跳转的页面地址
    }

    public static void main(String[] args) {
        // 加密+加盐，是通过 base64 编码以及添加 $ 等字符串组成
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("123456");
        String hashpw = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println(BCrypt.checkpw("123456",encode));    // true
    }
}
