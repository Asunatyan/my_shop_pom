package com.tamakiakoo.rabbitmq_springboot_provider;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue getQueue() {
        System.out.println("-------------------------------getQueue>");
        return new Queue("sb_queue");
    }

}
