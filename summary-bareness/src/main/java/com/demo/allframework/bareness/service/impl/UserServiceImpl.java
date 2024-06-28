package com.demo.allframework.bareness.service.impl;

import com.demo.allframework.bareness.entity.User;
import com.demo.allframework.bareness.repository.UserRepository;
import com.demo.allframework.bareness.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public User findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public int save(User user) {
        return userRepository.save(user);
    }

    @Override
    public int update(User user) {
        return userRepository.update(user);
    }

    @Override
    public Integer delete(long id) {
        return userRepository.delete(id);
    }

}
