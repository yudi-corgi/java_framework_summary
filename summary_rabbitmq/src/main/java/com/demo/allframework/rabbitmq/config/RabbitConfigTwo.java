package com.demo.allframework.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列配置
 * @author YUDI
 * @date 2021/1/3 22:44
 */
@Configuration
public class RabbitConfigTwo {

    private static final String TEST_EXCHANGE_NAME = "test_topic_exchange";
    private static final String DEAD_EXCHANGE_NAME = "dlx_exchange";
    private static final String TEST_QUEUE_NAME = "test_topic_queue";
    private static final String DEAD_QUEUE_NAME = "dlx_queue";

    @Bean
    public Exchange testTopicExchange(){
        return ExchangeBuilder.topicExchange(TEST_EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 普通队列设置死信交换机
     * @return
     */
    @Bean
    public Queue testTopicQueue(){
        Map<String,Object> args = new HashMap<>();
        // 设置死信交换机及其路由键
        args.put("x-dead-letter-exchange",DEAD_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key","dead.test");
        // 设置队列消息的统一过期时间，单位毫秒
        args.put("x-message-ttl",20000);
        // 设置队列最大长度，即消息存储数量
        args.put("x-max-length",10);
        // withArgument 设置队列消息的统一过期时间，单位毫秒
        return QueueBuilder.durable(TEST_QUEUE_NAME).withArguments(args).build();
    }

    @Bean
    public Binding testTopicBinding(@Qualifier("testTopicExchange") Exchange exchange, @Qualifier("testTopicQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("user.#").noargs();
    }

    /**
     * 创建死信交换机
     */
    @Bean
    public Exchange deadLetterExchange(){
        return ExchangeBuilder.topicExchange(DEAD_EXCHANGE_NAME).durable(false).build();
    }
    /**
     * 创建死信队列
     * @return
     */
    @Bean
    public Queue deadLetterQueue(){
        return QueueBuilder.nonDurable(DEAD_QUEUE_NAME).build();
    }

    /**
     * 绑定死信交换机和死信队列
     * @param dlx
     * @param dlq
     * @return
     */
    @Bean
    public Binding deadBinding(@Qualifier("deadLetterExchange") Exchange dlx, @Qualifier("deadLetterQueue") Queue dlq){
        return BindingBuilder.bind(dlq).to(dlx).with("dead.#").noargs();
    }


}
