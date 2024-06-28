package com.demo.allframework.bareness.service;

import com.demo.allframework.bareness.entity.User;

public interface UserService {

    User findById(long id);

    int save(User user);

    int update(User user);

    Integer delete(long id);

}
