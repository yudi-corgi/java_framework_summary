package com.demo.allframework.bareness.service.impl;

import com.demo.allframework.bareness.entity.TestDTO;
import com.demo.allframework.bareness.service.IBizService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BizServiceImpl implements IBizService {

    private final Map<Long, TestDTO> DATA_SOURCE = new HashMap<>(16);

    @Override
    public TestDTO select(Long id) {

        System.out.println("Input param：" + id);

        TestDTO result = DATA_SOURCE.get(id);
        return result != null ? result : new TestDTO().setId(1L).setAmount(new BigDecimal("9.99"))
                .setNumber(9527).setText("PASS").setCollection(List.of("nothing"));
    }

    @Override
    public Long create(TestDTO request) {
        DATA_SOURCE.put(request.getId(), request);
        return request.getId();
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
