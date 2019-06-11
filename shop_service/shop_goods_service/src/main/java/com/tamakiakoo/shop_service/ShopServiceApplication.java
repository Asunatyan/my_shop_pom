package com.tamakiakoo.shop_service;


import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.tamakiakoo.mapper")
@DubboComponentScan("com.tamakiakoo.Service")
@SpringBootApplication(scanBasePackages = "com.tamakiakoo")
public class ShopServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopServiceApplication.class,args);
    }
}
