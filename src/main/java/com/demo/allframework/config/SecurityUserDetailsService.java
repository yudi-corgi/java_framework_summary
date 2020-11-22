package com.demo.allframework.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * UserDetailsService 实现类，控制 UserDatails 的获取方式
 * @author YUDI
 * @date 2020/11/22 22:44
 */
public class SecurityUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // 通过 Security 的 User 对象构建
        // UserDetails build = User.withUsername("admin").password("123456").authorities("admin").build();

        // 数据库获取用户信息，手动封装 UserDetails 对象

        return null;
    }
}
