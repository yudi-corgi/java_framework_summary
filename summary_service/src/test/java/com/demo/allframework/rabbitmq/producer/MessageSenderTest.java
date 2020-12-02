package com.demo.allframework.rabbitmq.producer;

import com.demo.allframework.rabbitmq.entity.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author YUDI
 * @date 2020/5/21 9:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageSenderTest {

    @Autowired
    private MessageSender messageSender;

    @Test
    public void sendPersonMessage() {
        Person person = new Person();
        person.setId(UUID.randomUUID().toString());
        person.setName("人");
        person.setMessageId(UUID.randomUUID().toString() + "$" + System.currentTimeMillis());
        messageSender.sendPersonMessage(person);
    }
}