package com.demo.allframework.bareness.service;


import com.demo.allframework.bareness.entity.TestDTO;

public interface IBizService {

    TestDTO select(Long id);

    Long create(TestDTO request);

    void update();

    void delete();

}
