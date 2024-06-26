package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.service.IBizService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private IBizService bizService;

    @Test
    public void get() {

    }

    @Test
    public void post() {

    }

}
