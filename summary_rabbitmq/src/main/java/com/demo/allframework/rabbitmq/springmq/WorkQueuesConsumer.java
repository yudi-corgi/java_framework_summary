package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * WorkQueues 工作队列消费者
 * @author YUDI
 * @date 2020/12/28 23:51
 */
@Component
public class WorkQueuesConsumer {

    @RabbitListener(queuesToDeclare = @Queue("work"))
    public void workQueueReceiveOne(String message){
        System.out.println("workQueues 消费者一：" + message);
    }

    @RabbitListener(queuesToDeclare = @Queue("work"))
    public void workQueueReceiveTwo(String message){
        System.out.println("workQueues 消费者二：" + message);
    }

}
