package com.demo.allframework.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        // withArgument 设置队列消息的统一过期时间，单位毫秒
        return QueueBuilder.durable(TOPIC_QUEUE_NAME).withArgument("x-message-ttl",10000).build();
    }

    @Bean
    public Binding topicBinding(@Qualifier("topicExchange") Exchange exchange, @Qualifier("topicQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("user.#").noargs();
    }

    /**
     * direct 交换机
     * @return Exchange
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
     * @return Exchange
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

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        // 设置消息发送的转换类型
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //设置 Mandatory 为 true，表示交换机路由消息失败执行返回回调函数，false 表示消息会被丢弃（默认）
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returnCallback);
        return rabbitTemplate;
    }

    /**
     * 定义确认回调函数
     */
    RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         * 消息确认回调，publisher -> exchange
         * @param correlationData   相关数据
         * @param ack   确认状态，true 发送成功，false 发送失败
         * @param cause  失败原因，成功时为 Null
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if(ack){
                System.out.println("消息发送成功");
            }else{
                System.out.println("消息发送失败，原因：" + cause);
            }
        }
    };

    /**
     * 定义返回回调函数
     */
    RabbitTemplate.ReturnsCallback returnCallback = returned -> {
        System.out.println("发送的消息对象："+returned.getMessage());
        System.out.println("错误码："+returned.getReplyCode());
        System.out.println("错误信息："+returned.getReplyText());
        System.out.println("交换机："+returned.getExchange());
        System.out.println("路由键："+returned.getRoutingKey());
    };

    /**
     * 配置消息消费时用 JSON 反序列化
     * @param connectionFactory 连接工厂
     * @return RabbitListenerContainerFactory
     */
    // @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 设置消费者消息接收手动确认
        // factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}
