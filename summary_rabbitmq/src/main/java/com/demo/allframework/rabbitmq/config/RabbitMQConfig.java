package com.demo.allframework.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ 交换机、队列配置
 * @author YUDI
 * @date 2021/1/2 13:47
 */
@Configuration
public class RabbitMQConfig {

    private static final String TOPIC_EXCHANGE_NAME = "boot_topic_exchange";
    private static final String DIRECT_EXCHANGE_NAME = "boot_direct_exchange";
    private static final String FANOUT_EXCHANGE_NAME = "boot_fanout_exchange";
    private static final String TOPIC_QUEUE_NAME = "boot_topic_queue";
    private static final String DIRECT_QUEUE_NAME = "boot_direct_queue";
    private static final String FANOUT_QUEUE_NAME = "boot_fanout_queue";

    /**
     * topic 交换机
     */
    @Bean
    public Exchange topicExchange(){
        return ExchangeBuilder.topicExchange(TOPIC_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue topicQueue(){
        return QueueBuilder.durable(TOPIC_QUEUE_NAME).build();
    }

    @Bean
    public Binding topicBinding(@Qualifier("topicExchange") Exchange exchange, @Qualifier("topicQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("user.#").noargs();
    }

    /**
     * direct 交换机
     * @return
     */
    @Bean
    public Exchange directExchange(){
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue directQueue(){
        return QueueBuilder.durable(DIRECT_QUEUE_NAME).build();
    }

    @Bean
    public Binding directBinding(@Qualifier("directExchange") Exchange exchange, @Qualifier("directQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("direct_g").noargs();
    }

    /**
     * fanout 交换机
     * @return
     */
    @Bean
    public Exchange fanoutExchange(){
        return ExchangeBuilder.fanoutExchange(FANOUT_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue fanoutQueue(){
        return QueueBuilder.durable(FANOUT_QUEUE_NAME).build();
    }

    @Bean
    public Binding fanoutBinding(@Qualifier("fanoutExchange") Exchange exchange, @Qualifier("fanoutQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    /**
     * 配置消息消费时用 JSON 反序列化
     * @param connectionFactory
     * @return
     */
    // @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}
