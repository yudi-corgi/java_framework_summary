package com.demo.allframework.shardingsphere.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.allframework.shardingsphere.entity.User;
import com.demo.allframework.shardingsphere.mapper.UserMapper;
import com.demo.allframework.shardingsphere.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author CDY
 * @date 2021/10/31
 * @description
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
