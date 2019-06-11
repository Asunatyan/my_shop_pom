package com.tamakiakoo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.tamakiakoo.Service.IUserService;
import com.tamakiakoo.entity.User;
import com.tamakiakoo.utils.Base64Util;
import com.tamakiakoo.utils.BaseResult;
import com.tamakiakoo.utils.CodeUtil;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.chrono.MinguoEra;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sso")
public class SSOController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private IUserService userService;

    @RequestMapping("/{page}")
    public String page(@PathVariable("page") String page) {

        return page;
    }

    @RequestMapping("/toRegister")
    public String toRegister() {

        return "register";
    }

    /*@RequestMapping("/toLogin")
    public String toLogin(String returnUrl,Model model) {
        if (returnUrl != null) {
            model.addAttribute("returnUrl", returnUrl);
        }
        return "login";
    }*/

    @RequestMapping("/toLogin")
    public String toLogin() {

        return "login";
    }


    @RequestMapping("/isLogin")
    @ResponseBody
    public String isLogin(
            @CookieValue(name="login_token", required=false)
                    String loginToken,
            @RequestParam(value = "loginToken", required = false)
                    String loginToken2,
            String callback){
        String result = null;
        //开始认证当前用户是否登录
        loginToken = loginToken != null ? loginToken : loginToken2;

        if (loginToken != null) {
            User user = ((User) redisTemplate.opsForValue().get(loginToken));
            ObjectMapper mapper = new ObjectMapper();
            try {
                // 反序列化 JSON 到对象Student student = mapper.readValue(jsonString, Student.class);
                //  序列化对象到 JSON
                result = mapper.writeValueAsString(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return callback != null ? callback + "(" + result + ")" : result;
        //return callback + "(" + result + ")";
    }

    @RequestMapping("/invalid")
    public String invalid(@CookieValue(name = "login_token", required = true)String loginToken, HttpServletResponse response){

        //删除redis的key
        redisTemplate.delete(loginToken);
        //清除客户端的cookie
        Cookie cookie = new Cookie("login_token", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "login";
    }


    @RequestMapping(value = "/login",produces = "html/text;charset=utf-8")
    public String login(String username, String password,String returnUrl, Model model, HttpServletResponse response) {
        //调用服务进行登录认证
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("message", "用户名或密码错误");
            return "login";
        }

        //登录成功了
        String token = UUID.randomUUID().toString();
        //token作为key，用户信息作为value 放入redis中
        redisTemplate.opsForValue().set(token, user);
        redisTemplate.expire(token, 7, TimeUnit.DAYS);

        //放入用户浏览器的cookie中
        Cookie cookie = new Cookie("login_token", token);
        cookie.setMaxAge(1 * 60 * 60 * 24 * 7);//最大超时时间
        cookie.setPath("/");//所有请求都会携带该cookie
        //cookie.setDomain(".sb.com");//所有二级域名共享cookie， cookie不能一级域名共享
        //该属性用来设置cookie是否只能通过http请求获取。s（document.cookie）就没办法再获得该cookie，增加cookie的安全性
        //cookie.setHttpOnly(true);
        //该属性设置为true之后，只有https请求才会携带该cookie，极大的提高了cookie安全性。
        //cookie.setSecure(true);

        response.addCookie(cookie);

        if (returnUrl != null && !returnUrl.trim().equals("")) {
           /* 决绝办法是在浏览器端就进行编码  但是为什么encode和js的encodeURIComponent(returnUrl);有什么区别呢
           try {
                //这样写地址就会变成http://localhost:8084/sso/http%3A%2F%2Flocalhost%3A8082%2Fsearch%2FgetByKeyWord%3Fkeyword%3D%E5%8D%8E%E4%B8%BA
                //变成两个http了
                //String url = URLEncoder.encode(returnUrl, "utf-8");
                return "redirect:" + url;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
            return "redirect:" + returnUrl;
        }
        return "redirect:http://localhost:8081";

    }


    @RequestMapping(value = "/register", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResult register(User user, String code) {
        String codeEmail = ((String) redisTemplate.opsForValue().get(user.getEmail() + "_code"));

        if (code == null || !code.equals(codeEmail)) {
            return BaseResult.fail("验证码错误~~");
        }

        //注册
        int register = userService.register(user);

        if (register == -1) {
            return BaseResult.fail("用户名已存在~~");
        }

        //redisTemplate.delete(user.getEmail() + "_code");

        if (register == -2) {
            return BaseResult.fail("邮箱已存在");
        }


        return BaseResult.success();
    }

    @RequestMapping(value = "/toUpdatePassword")
    public String toUpdatePassword(String username, String token, Model model) {

        if (username != null && token != null) {
            model.addAttribute("username", Base64Util.decodingBase64(username));
            model.addAttribute("token", token);
            return "updatePassword";
        }

        return "error";
    }

    @RequestMapping(value = "/updatePassword")
    public String updatePassword(String username, String token, String password ,Model model) {

        String t = (String) redisTemplate.opsForValue().get(username + "_forget_token");
        if (t == null && !t.equals(token)) {
            return "error";
        }
        userService.updatePassword(username,password);

        redisTemplate.delete(username + "forget_token");

        return "succ";
    }

    @RequestMapping(value = "/sendForgetMail", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResult sendForgetMail(String username) {

        //t通过用户名获取对象
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return BaseResult.fail("用户名不存在");
        }

        //说明账号存在
        String email = user.getEmail();//123456@qq.com
        int index = email.indexOf("@");
        String replaceEmail = null;
        if (index > 4) {
            String substring = email.substring(4, index);
            replaceEmail = email.replace(substring, "*****");
        }else {
            String substring = email.substring(0, index);
            replaceEmail = email.replace(substring, substring + "*******");
        }

        //给email发送找回密码的邮件（是一个url，点击这个url可以跳转到修改密码的页面）
        //时效性、一次性、独立性
        //http://localhost:8084/sso/toUpdatePassword?username=zhangsan

        String toMail = "http://mail." + email.substring(index + 1);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        String token = UUID.randomUUID().toString();////硬件（网卡、主板、mac...） + 当前时间 + ...

        redisTemplate.opsForValue().set(user.getUsername()+"_forget_token", token);
        redisTemplate.expire(user.getUsername()+"_forget_token", 10, TimeUnit.MINUTES);

        try {
            //设标题
            messageHelper.setSubject("xxx找回密码");

            //设置发送者
            messageHelper.setFrom("254497414@qq.com");

            //设置接收者
            messageHelper.setTo(email);

            //设置邮箱内容 第二个参数是是否解析html便签,不设置成ture就会在邮箱里面显示出来
            //
            String url = "http://localhost:8084/sso/toUpdatePassword?username=" + Base64Util.encodingBase64(user.getUsername()) + "&token=" + token;
            String emailContent = "找回密码请点击<a href='" + url + "'>这里</a>";

            messageHelper.setText(emailContent, true);

            //设置附件
            //MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);需要这设置才能发送附件
            //messageHelper.addAttachment("xxx", new InputStreamResource(new FileInputStream("xxx")));

            //mailSender.send(mimeMessage);
            System.out.println(url);
            Map map = new HashMap();
            map.put("tomail", toMail);
            map.put("replaceEmail", replaceEmail);

            return BaseResult.success("发送成功",map);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return BaseResult.success();
    }


    @RequestMapping(value = "/sendEmail", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResult sendEmail(String email) {
        //jackson
        ObjectMapper objectMapper = new ObjectMapper();

        //创建一封邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        //创建一个邮件帮助类
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        int code = CodeUtil.getCode();
        redisTemplate.opsForValue().set(email + "_code", code + "");
        redisTemplate.expire(email + "_code", 5, TimeUnit.MINUTES);
        try {
            //设标题
            messageHelper.setSubject("xxx注册邮件");
            //设置发送者
            messageHelper.setFrom("254497414@qq.com");

            //设置接收者
            messageHelper.setTo(email);

            //设置邮箱内容 第二个参数是是否解析html便签,不设置成ture就会在邮箱里面显示出来
            messageHelper.setText("验证码:" + code, true);

            //设置附件
            //MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);需要这设置才能发送附件
            //messageHelper.addAttachment("xxx", new InputStreamResource(new FileInputStream("xxx")));

            //mailSender.send(mimeMessage);
            System.out.println("验证码" + code);
            return BaseResult.success("发送成功");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return BaseResult.fail("发送失败");

    }


    @RequestMapping("sayHi")
    @ResponseBody
    public String sayHi(){
        User user = new User();
        user.setPassword("123");
        user.setEmail("123");
        user.setUsername("123");
        user.setId(1);

        String s = JSON.toJSONString(user);
        return s;
    }

}
