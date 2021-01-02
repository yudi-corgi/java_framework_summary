package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring 整合 RabbitMQ 生产者
 * @author YUDI
 * @date 2020/12/28 22:59
 */
@RestController
@RequestMapping("/")
public class Producer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * Hello World
     */
    @GetMapping("send")
    public void sending(){
        // 最基本的 hello word 模型
        // 参数一：指定 routingKey，当交换机为默认时，会自动查询与 routingKey 相同名称的队列(没有则创建)并发送消息
        // 参数二：消息内容，Object 类型
        rabbitTemplate.convertAndSend("hello","Hello World!");
    }

    /**
     * workQueues：工作队列
     */
    @GetMapping("workQueues")
    public void workQueues(){
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("work","Hello work queues!");
        }
    }

    /**
     * fanout：广播模式
     */
    @GetMapping("fanout")
    public void fanout(){
        // 指定交换机名称，路由键，消息
        // 广播模式下路由键无意义，为 "" 即可
        rabbitTemplate.convertAndSend("notice","","Hello fanout!");
    }

    @GetMapping("configFanout")
    public void configFanout(){
        // 发送消息到 RabbitMQConfig 配置的交换机
        rabbitTemplate.convertAndSend("boot_fanout_exchange","","config fanout!");
    }

    /**
     * direct：直连模式
     */
    @GetMapping("direct/{routingKey}")
    public void direct(@PathVariable String routingKey){
        // 指定交换机名称，路由键，消息
        rabbitTemplate.convertAndSend("logs",routingKey,"Hello direct! RoutingKey is " + routingKey);
    }

    @GetMapping("configDirect/{routingKey}")
    public void configDirect(@PathVariable String routingKey){
        // 发送消息到 RabbitMQConfig 配置的交换机
        rabbitTemplate.convertAndSend("boot_direct_exchange",routingKey,"config direct! RoutingKey is " + routingKey);
    }

    /**
     * topics：主题模式
     */
    @GetMapping("topic/{routingKey}")
    public void topic(@PathVariable String routingKey){
        // 指定交换机名称，路由键，消息
        rabbitTemplate.convertAndSend("topics",routingKey,"Hello topic! RoutingKey is " + routingKey);
    }

    @GetMapping("configTopic/{routingKey}")
    public void configTopic(@PathVariable String routingKey){
        // 设置发送时消息对象使用 JSON 序列化
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        // 发送消息到 RabbitMQConfig 配置的交换机
        rabbitTemplate.convertAndSend("boot_topic_exchange", routingKey, "config topic! RoutingKey is " + routingKey);
    }

}
