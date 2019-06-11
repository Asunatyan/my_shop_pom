package com.tamakiakoo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamakiakoo.Service.ICartService;
import com.tamakiakoo.Service.IGoodsService;
import com.tamakiakoo.aop.IsLogin;
import com.tamakiakoo.entity.Cart;
import com.tamakiakoo.entity.Goods;
import com.tamakiakoo.entity.User;
import com.tamakiakoo.utils.TestAnno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Reference
    private ICartService cartService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private IGoodsService goodsService;


    /**
     * 将商品添加到购物车
     *      先通过aop获取user对象,如果是空说明没有登录则,将商品信息存入到redis里面,否则存入到数据库里面
     * //@IsLogin注解如果value是true表示必须登录,默认false
     *
     *
     * @param cart
     * @param user
     * @param cartToken
     * @param response
     * @param callback
     * @return
     */
    @IsLogin
    @RequestMapping("/addCart")
    @ResponseBody
    public String addCart(Cart cart, User user, @CookieValue(value = "cartToken",required = false) String cartToken, HttpServletResponse response, String callback) {

        System.out.println("商品id---->" + cart.getGid()+ "==========商品数量---->" + cart.getGnumber());
        System.out.println("当前是否登录---------->"+user);
        if (user != null) {
            //已经登录的状态--->数据库
            //设置购物车所属的用户
            cart.setUid(user.getId());

            //根据商品id查询改山商品信息
            Goods goods = goodsService.getById(cart.getGid());

            //设置购物车的商品价格
            cart.setGprice(goods.getGprice());
            cart.setXiaoji(goods.getGprice().multiply(BigDecimal.valueOf(cart.getGnumber())));
            cart.setCreatetime(new Date());

            //调用服务层保存数据
            cartService.addCart(cart);

        }else {
            //未登录的转态--->redis
            //生成一个购物车令牌
            //String cartToken = UUID.randomUUID().toString();写每次都会深成一个新的cookie
            cartToken = cartToken != null ? cartToken : UUID.randomUUID().toString();
            //将购物车的信息保存到redis中
            //集合-----双向链表
            redisTemplate.opsForList().rightPush(cartToken, cart);
            redisTemplate.expire(cartToken, 365, TimeUnit.DAYS);
            //将cartToken写入cookie中国
            Cookie cookie = new Cookie("cartToken", cartToken);
            response.addCookie(cookie);
            cookie.setPath("/");
            cookie.setMaxAge(1000 * 60 * 60 * 12 * 365);

        }

        return callback+"('添加成功~~')";
    }


    /**
     * 前端获取购物车信息
     * @param carToken
     * @param user
     * @param callback
     * @return
     */
    @IsLogin
    @RequestMapping("list")
    @ResponseBody
    public String cartListAjax(@CookieValue(value = "cart_token", required = false) String carToken, User user,String callback) {

        List<Cart> cartList = cartService.getCartList(carToken, user);

        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
                    s = mapper.writeValueAsString(cartList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return callback +"("+s+")";
    }

    /**
     * 展示购物车列表
     *
     * @return
     */
    @IsLogin
    @RequestMapping("/showlist")
    @ResponseBody
    public String showCartList(){

        return null;
    }

}
