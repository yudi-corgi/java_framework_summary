package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.entity.TestDTO;
import com.demo.allframework.bareness.service.IBizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
    @Resource
    private ObjectMapper objectMapper;

    // @MockBean 与 @SpyBean 区别：前者是 Mockito mock 的对象，使用时并不会真实调用其方法，后者则会真实监听调用
    @SpyBean
    private IBizService bizService;

    @BeforeAll
    static void setup() {
        // @BeforeAll 指示该方法在当前测试类的测试方法执行前有且仅执行一次
        // @BeforeEach 指示该方法在当前测试类的所有方法执行前都会先执行一次
        System.out.println("执行一次");
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

        bizService.select(1L);

        // 校验 bizService#select 是否被调用一次并且入参类型为 Long
        verify(bizService, times(1)).select(ArgumentMatchers.anyLong());

    }

    @Test
    public void post() throws Exception {

        TestDTO request = new TestDTO().setId(1L).setText("测试额呢绒").setAmount(BigDecimal.TEN)
                .setNumber(6972).setCollection(List.of("banana"));

        // when 是非 void 返回值的方法存根，thenReturn 会让方法调用固定返回指定的值
        // isA 表示调用 create 时匹配的参数类型为指定类型时，存根才会生效
        // 但是这种方式在使用 @SpyBean 注入对象时会去执行实际代码，然而因为没有入参，会导致 NPE
        // when(bizService.create(any(TestDTO.class))).thenReturn(5L);
        // 正确做法如下：
        doReturn(5L).when(bizService).create(any());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_URL.concat("/post"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        System.out.println("返参：" + mvcResult.getResponse().getContentAsString());

        verify(bizService, times(1)).create(isA(TestDTO.class));
    }

    @Test
    public void delete() throws Exception {
        // doNothing 是 bizService.delete 方法调用的存根
        // doNothing 的作用是让返回值为 void 的方法不执行任何操作，简单说就是不执行方法里的代码
        // 存根的意思是把 doNothing 的效果在方法首次调用后，如果有多个存根，那么排序生效
        doNothing().when(bizService).delete();
        bizService.delete();
        bizService.delete();


        // mockMvc.perform(MockMvcRequestBuilders
        //         .delete(CONTROLLER_URL.concat("/delete"))
        //         .contentType(MediaType.APPLICATION_JSON))
        //         .andExpect(status().isOk())
        //         .andDo(print());

    }

    @Test
    public void update() {

    }

}
