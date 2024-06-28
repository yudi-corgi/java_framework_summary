package com.demo.allframework.bareness.repository;

import com.demo.allframework.bareness.entity.User;

public interface UserRepository {

    User findById(long id);

    int save(User user);

    int update(User user);

    int delete(long id);

}
