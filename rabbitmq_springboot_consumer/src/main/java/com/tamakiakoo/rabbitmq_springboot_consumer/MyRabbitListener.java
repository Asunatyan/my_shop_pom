package com.tamakiakoo.rabbitmq_springboot_consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRabbitListener {

    @RabbitListener(queues = "sb_queue")
    public void myHandler(String msg) {
        System.out.println("监听到RabbitMQ发送过来的消息: " + msg);
    }

}
