package com.demo.allframework.bareness.service;


import com.demo.allframework.bareness.entity.TestDTO;

public interface IBizService {

    TestDTO select();

    Long create();

    void update();

    void delete();

}
