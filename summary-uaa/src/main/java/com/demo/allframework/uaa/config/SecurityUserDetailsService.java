package com.demo.allframework.uaa.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.demo.allframework.uaa.entity.SysPermission;
import com.demo.allframework.uaa.entity.SysRole;
import com.demo.allframework.uaa.entity.SysUser;
import com.demo.allframework.uaa.service.SysPermService;
import com.demo.allframework.uaa.service.SysRoleService;
import com.demo.allframework.uaa.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsService 实现类，控制 UserDetails 的获取方式
 * @author YUDI
 * @date 2020/11/22 22:44
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserService userService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysPermService permService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过 Security 的 User 对象构建
        // UserDetails build = User.withUsername("admin").password("123456").authorities("admin").roles("admin","user").build();

        // 数据库获取用户信息，手动封装 UserDetails 对象
        LambdaQueryWrapper<SysUser> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysUser::getLoginName,username);
        SysUser user = userService.getOne(lambdaQuery);
        if(user == null){
            // 若为 null，返回后由 DaoAuthenticationProvider 处理
            return null;
        }
        String[] roleArr = loadRoleByUserId(user.getId());
        String[] permArr = loadPermByUserId(user.getId());
        return User.withUsername(user.getName()).password(user.getPassword()).authorities(permArr).roles(roleArr).build();
    }

    private String[] loadRoleByUserId(Long userId){
        LambdaQueryWrapper<SysRole> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.apply("role_id in (select role_id from sys_user_role where user_id = {0})", userId)
                .select(SysRole::getRoleKey);
        List<Object> roleList = roleService.listObjs(lambdaQuery);
        String[] roleArr = new String[roleList.size()];
        for (int i = 0; i < roleList.size(); i++) {
            roleArr[i] = (String) roleList.get(i);
        }
        return roleArr;
    }

    /**
     * 通过用户ID查询其权限数据
     * @param userId
     * @return
     */
    private String[] loadPermByUserId(Long userId){
        LambdaQueryWrapper<SysPermission> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.apply("perm_id in (select perm_id from sys_role_perm where role_id in (select role_id from sys_user_role where user_id = {0}))",userId)
                .select(SysPermission::getPermCode);
        List<Object> permList = permService.listObjs(lambdaQuery);
        String[] permArr = new String[permList.size()];
        for (int i = 0; i < permList.size(); i++) {
            permArr[i] = (String) permList.get(i);
        }
        return permArr;
    }

}
