package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.entity.User;
import com.demo.allframework.bareness.repository.UserRepository;
import com.demo.allframework.bareness.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String CONTROLLER_URL = "/user/";

    @Resource
    private MockMvc mockMvc;
    @Resource
    private ObjectMapper objectMapper;
    @SpyBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    public void findById() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("EEEE");
        user.setAge(13);
        user.setBirthday(LocalDateTime.now());
        when(userRepository.findById(anyLong())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_URL.concat("findById"))
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(13))
                .andDo(print())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());

        verify(userService, times(1)).findById(anyLong());

    }

    @Test
    public void save() throws Exception {

        User user = new User().setId(5L).setAge(22).setName("WWWW").setBirthday(LocalDateTime.now());
        // userService.save(user);
        when(userRepository.save(isA(User.class))).thenReturn(1);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_URL.concat("save"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1))
                .andDo(print())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());

        verify(userService, times(1)).save(isA(User.class));

    }

    @Test
    public void update() throws Exception {

        User user = new User().setId(5L).setAge(3).setName("SSSS").setBirthday(LocalDateTime.now());

        // doReturn(1).when(userService).update(isA(User.class));

        mockMvc.perform(MockMvcRequestBuilders
                .put(CONTROLLER_URL.concat("update"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1))
                .andDo(print());

        verify(userService, times(1)).update(isA(User.class));
    }

    @Test
    public void delete() throws Exception {

        when(userRepository.delete(anyLong())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders
                .delete(CONTROLLER_URL.concat("delete/6"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1))
                .andDo(print());

        verify(userService, times(1)).delete(anyLong());
    }

}
