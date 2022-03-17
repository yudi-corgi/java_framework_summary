package com.demo.allframework.rocketmq.rmqboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author YUDI-Corgi
 * @Description
 */
@RestController
@Slf4j
public class RmqController {

    @Resource
    private RmqProducerService producerService;

    @GetMapping("/send")
    public SendResult send() {
        String content = "Hello, RocketMQ with SpringBoot";
        return producerService.sendEasy(content, "testTopic", "producerGroup");
    }

    @GetMapping("/sendDelay")
    public SendResult sendDelay() {
        String content = "Delay consumer msg";
        // 两秒后消费
        return producerService.sendNotTag(content, "testTopic", "producerGroup", 1);
    }
}
