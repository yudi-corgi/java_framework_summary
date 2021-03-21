package com.demo.allframework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
// 该注解表示开启基于注解的安全拦截，如 prePostEnabled:{@preAuthorize,@postAuthorize} securedEnabled:{@Secured}
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 定义用户信息服务（查询用户信息）
     * @return
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService(){
        // 创建 InMemoryUserDetailsManager 对象，构建用户账户信息保存在内存当中
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password(BCrypt.hashpw("admin",BCrypt.gensalt())).authorities("admin").build());
        manager.createUser(User.withUsername("tourist").password(BCrypt.hashpw("123456",BCrypt.gensalt())).authorities("tourist").build());
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
                // 会话管理
                // ALWAYS：没有就创建 Session，IF_REQUIRED：默认，若需要则创建（登录时为用户创建 Session）
                // NEVER：Security 不会创建 Session，但若其它地方创建了则会使用它
                // STATELESS：不会创建也不会使用 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                // .invalidSessionUrl("/sys?error=INVALID_SESSION")      // session 超时（无效）时跳转地址，此处为登录页
            .and()
                .authorizeRequests()
                // web 授权
                // .antMatchers("/sys/t/t1").hasAuthority("admin")     // 表示拥有 admin 权限才可以访问
                // .antMatchers("/sys/t/t2").hasAuthority("tourist")     // 表示拥有 user 权限才可以访问
                // .antMatchers("").hasIpAddress("127.0.0.1")       // 根据 IP 或子网限制访问
                // 根据 SpringEL 表达式描述权限
                // .antMatchers("").access("hasAuthority('admin') and hasAnyAuthority('tourist')")
                // .antMatchers("/sys/t/**").authenticated()     // 表示 /sys/** 的请求都需要认证
                .anyRequest().permitAll()       // 除了上面配置的请求路径，其余请求通过，
            .and()
                .formLogin()    // 允许表单登录
                .loginPage("/sys")     // 设置登录地址
                .loginProcessingUrl("/sys/login")   // 设置登录处理的接口地址，Security 默认是 */login*
                .defaultSuccessUrl("/sys/loginSuccess")   // 登录后跳转的页面地址
            .and()
                .logout()
                .logoutUrl("/sys/logout")        // 设置触发退出操作的接口地址
                .logoutSuccessUrl("/sys?logoutSuccess");     // 退出成功时跳转地址，此处为登录页
                // 设置退出时的处理器，实现 LogoutHandler 接口的 logout() 方法，若有设置，logoutSuccessUrl 将无效
                // .addLogoutHandler(logoutHandler)
                // deleteCookies 是 LogoutHandler 的一种实现，清理 cookie
                // .deleteCookies();
                // .invalidateHttpSession(true);    // 退出时让 httpSession 无效，默认为 true
    }

    public static void main(String[] args) {
        // 加密+加盐，是通过 base64 编码以及添加 $ 等字符串组成
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("secret");

        System.out.println(encode);
        String hashpw = BCrypt.hashpw("admin", BCrypt.gensalt());
        System.out.println(hashpw);
        System.out.println(hashpw.length());
        System.out.println(BCrypt.checkpw("123456",encode));    // true
    }
}
