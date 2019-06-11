package com.tamakiakoo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tamakiakoo.Service.IUserService;
import com.tamakiakoo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Reference
    private IUserService userService;

    @RequestMapping("/login")
    public String login(String username, String password, Model model, HttpSession session) {

        User user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "index";
        }
        model.addAttribute("msg", "账号或密码错误");
        return "login";
    }

    @RequestMapping("/invalid")
    public String invalid(HttpSession session) {
        session.invalidate();
        return "login";
    }

}
