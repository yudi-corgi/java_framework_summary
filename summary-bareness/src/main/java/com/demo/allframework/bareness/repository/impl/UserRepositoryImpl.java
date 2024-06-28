package com.demo.allframework.bareness.repository.impl;

import com.demo.allframework.bareness.entity.User;
import com.demo.allframework.bareness.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    // @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public User findById(long id) {

        return jdbcTemplate.queryForObject("select * from p_user where id = ?", User.class, id);

    }

    @Override
    public int save(User user) {

        return jdbcTemplate.update("INSERT INTO p_user(id, name, age, birthday) values (?, ?, ?, ?)",
                user.getId(), user.getName(), user.getAge(), user.getBirthday());

    }

    @Override
    public int update(User user) {

        return jdbcTemplate.update("UPDATE p_user SET name = ?, age = ?, birthday = ? where id = ?",
                user.getName(), user.getAge(), user.getBirthday(), user.getId());

    }

    @Override
    public int delete(long id) {

        return jdbcTemplate.update("DELETE FROM p_user WHERE id = ?", id);

    }

}
