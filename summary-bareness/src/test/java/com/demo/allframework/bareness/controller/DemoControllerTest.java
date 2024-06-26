package com.demo.allframework.bareness.controller;

import com.alibaba.fastjson.JSON;
import com.demo.allframework.bareness.entity.TestDTO;
import com.demo.allframework.bareness.service.IBizService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTest {

    private static final HttpHeaders httpHeaders = new HttpHeaders();
    private static final String CONTROLLER_URL = "/biz";

    @Resource
    private MockMvc mockMvc;
    @MockBean
    private IBizService bizService;

    @BeforeAll
    static void setup() {
        // @BeforeAll 指示该方法在当前测试类的所有方法执行前有且仅执行一次
        // @BeforeEach 指示该方法在当前测试类的所有方法执行前都会先执行一次
    }

    @Test
    public void get() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_URL.concat("/get"))
                        .contentType(MediaType.APPLICATION_JSON)
                        // 请求参数
                        .param("id", "1"))
                // 预期结果匹配
                .andExpect(status().isOk())
                // 用表达式取结果中的字段匹配预期值
                .andExpect(jsonPath("$.text").value("PASS"))
                // 打印 MvcResult 结果详情并返回结果
                .andDo(print()).andReturn();

        System.out.println(mvcResult.getRequest());
        System.out.println(mvcResult.getResponse());

        verify(bizService, times(1)).select(ArgumentMatchers.anyLong());

    }

    @Test
    public void post() throws Exception {

        TestDTO request = new TestDTO().setId(1L).setText("测试额呢绒").setAmount(BigDecimal.TEN)
                .setNumber(6972).setCollection(List.of("banana"));

        // doNothing 是 bizService.create 方法调用的存根
        // doNothing 的作用是让返回值为 void 的方法不执行任何操作，简单说就是不执行方法里的代码
        // 存根的意思是把 doNothing 的效果放在方法首次调用后，如果有多个存根，那么排序生效
        // doNothing().doThrow(new RuntimeException("存根二报错")).when(bizService).create(isA(TestDTO.class));

        when(bizService.create(isA(TestDTO.class))).thenReturn(5L);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_URL.concat("/post"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        System.out.println("返参：" + mvcResult.getResponse().getContentAsString());

        verify(bizService, times(1)).create(isA(TestDTO.class));
    }

    @Test
    public void delete() {

    }

    @Test
    public void update() {

    }

}
