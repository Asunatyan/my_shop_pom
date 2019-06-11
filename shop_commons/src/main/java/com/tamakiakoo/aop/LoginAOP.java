package com.tamakiakoo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamakiakoo.entity.User;
import com.tamakiakoo.utils.HttpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * 获取用户登录的信息,根据用户的cookie获取user对象,
 *      如果有就说明登录了并把usr对象注入到目标方法的User参数里面
 * 在根据注解(IsLogin)判断是否需要登录操作
 *      如果需要就直接返回到登录页面
 */
@Aspect
@Component//记住这个包一定要被spring容器扫面到,扫不到就需要@Bean
public class LoginAOP {
    /**
     * 环绕增强
     */
    @Around("execution(* *.*.controller.*.*(..)) && @annotation(IsLogin)")
    public Object isLogin(ProceedingJoinPoint joinPoint) {

        //获取httpservletReqeust对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        //-------------------判断是否登录-----------------------
        //1、获得浏览器发送的cookie
        String loginToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login_token")) {
                    loginToken = cookie.getValue();
                    break;
                }
            }
        }

        //登录的用户信息
        User user = null;

        if (loginToken!=null){
            //有可能登录
            //2、调用sso工程判断是否登录
            String userJson = HttpUtil.sendGet("http://localhost:8084/sso/isLogin?loginToken=" + loginToken);
            if (userJson!=null){
                //登录成功
                ObjectMapper mapper = new ObjectMapper();
                try {
                    user = mapper.readValue(userJson, User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(user == null){
            //说明当前浏览器并未登录
            //如果未登录判断IsLogin注解的mustLogin返回值是否为

            //通过环绕增强的参数对象获得目标的签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            //通过签名获得目标方法的反射对象
            Method method = signature.getMethod();

            //通过反射对象获得方法上的注解
            IsLogin isLogin = method.getAnnotation(IsLogin.class);

            //判断这个注解的方法返回值
            if(isLogin.mustLogin()){
                //说明当前需要强制登录

                //获得当前请求的路径（这个路径不会携带？后面的参数）
                String path = request.getRequestURL().toString();
                //获得请求?后面的参数列表
                String params = request.getQueryString();
                //最终的回跳路径
                String returnUrl = path + "?" + params;

                try {
                    returnUrl = URLEncoder.encode(returnUrl, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return "redirect:http://localhost:8084/sso/tologin?returnUrl=" + returnUrl;
            }
        }


        //修改目标方法形参列表

        //获得目标方法的所有参数列表
        Object[] args = joinPoint.getArgs();
        if(args != null){
            //以此循环所有参数对象
            for (int i = 0; i < args.length; i++) {
                //如果一个参数的类型是User类型
                if(args[i] != null && args[i].getClass() == User.class){
                    //修改这个位置的参数
                    args[i] = user;
                }
            }
        }


        //-------------------调用目标方法，并且根据登录状态修改形参列表--------------------------
        Object result = null;
        try {
            result = joinPoint.proceed(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }
}
