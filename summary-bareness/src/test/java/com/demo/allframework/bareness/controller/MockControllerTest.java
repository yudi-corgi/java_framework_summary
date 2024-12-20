package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.entity.User;
import com.demo.allframework.bareness.repository.UserRepository;
import com.demo.allframework.bareness.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 指定测试类的声明周期，默认为 PER_METHOD，即每个测试方法执行前都会创建一个新的测试实例，PER_CLASS 为整个测试类执行前创建一个新的测试实例。
 * </p>
 * <p>
 * 但并不推荐这么做，因为共用一个测试实例，每个 Mock 对象调用方法次数在不同的测试方法中是会累加的，即同样的方法，在测试 A 方法和测试 B 方法中
 * 都调用了，总调用次数是 2，而 verify() 校验方法默认是验证调用次数 1，因此如果有重复调用方法的地方可能出现次数调用验证不通过。
 * </p>
 * <div>推荐：
 * <p>在构造器或 @BeforeEach 注解方法中开启 openMocks(this)；</p>
 * <p>或者在清楚哪个 Mock 对象被重复调用时，可以在 @BeforeEach 方法中使用 Mockito.reset(mockObject) 重置调用次数。</p>
 * </div>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MockControllerTest {

    @InjectMocks
    private UserServiceImpl mockUserService;

    @Mock
    private UserRepository userRepository;

    /**
     * {@link BeforeAll} 注解是在所有方法之前会调用一次，因此如果有共享的环境变量时，在测试类的
     * 生命周期为 {@link TestInstance.Lifecycle#PER_METHOD} 时，必须将方法设置为静态，而如
     * 果是 {@link TestInstance.Lifecycle#PER_CLASS} ，因为测试实例只会创建一次，则无需使用 static 修饰为静态。
     */
    @BeforeAll
    void setup() {
        // 该方法作用：为给定的测试类初始化使用 Mockito 注解进行注解的对象：@Mock、@Spy、@Captor、@InjectMocks
        // 如果测试类使用了 @SpringBootTest 或 @RunWith(SpringRunner.class) 等注解时，则会启动 Spring 上下文
        // 运行测试类，如果测试类中并没有使用 Spring 相关的注解或依赖，那么效率会很低；当纯粹使用 Mockito 的注解时，则无需添加注解
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setId(0L);
        user.setName("0.0");
        user.setAge(0);
        user.setBirthday(LocalDateTime.now());

        // 打桩
        when(userRepository.findById(anyLong())).thenReturn(user);

        // 执行方法
        User result = mockUserService.findById(0L);
        System.out.println(result.toString());

        // 验证结果
        verify(userRepository).findById(0L);
        assertThat(result).isEqualTo(user);
    }

}
