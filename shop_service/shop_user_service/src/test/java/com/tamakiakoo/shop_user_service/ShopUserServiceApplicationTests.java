package com.tamakiakoo.shop_user_service;

import com.tamakiakoo.Service.IUserService;
import com.tamakiakoo.service.impl.IUserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopUserServiceApplicationTests {

    @Autowired
    private IUserService userService;

    @Test
    public void contextLoads() {
        System.out.println(userService.login("admin", "admin"));
    }

}
