package com.tamakiakoo.shop_service;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue getQueue1(){
        return new Queue("search_queue");
    }

    @Bean
    public Queue getQueue2() {
        return new Queue("item_queue");
    }

    @Bean
    public FanoutExchange getExchange(){
        return (FanoutExchange) ExchangeBuilder.fanoutExchange("goods_exchange").build();
    }

    /**
     * 队列和交换机进行绑定
     */
    @Bean
    public Binding bind1(Queue getQueue1, FanoutExchange getExchange){
        return BindingBuilder.bind(getQueue1).to(getExchange);
    }

    @Bean
    public Binding bind2(Queue getQueue2, FanoutExchange getExchange){
        return BindingBuilder.bind(getQueue2).to(getExchange);
    }

}
