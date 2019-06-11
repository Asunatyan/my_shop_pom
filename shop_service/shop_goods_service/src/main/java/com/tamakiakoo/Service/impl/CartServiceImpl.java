package com.tamakiakoo.Service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tamakiakoo.Service.ICartService;
import com.tamakiakoo.Service.IGoodsService;
import com.tamakiakoo.entity.Cart;
import com.tamakiakoo.entity.Goods;
import com.tamakiakoo.entity.User;
import com.tamakiakoo.mapper.ICartMapper;
import com.tamakiakoo.mapper.IGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private ICartMapper cartMapper;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public int addCart(Cart cart) {

        return cartMapper.insert(cart);
    }

    /**
     * 获得用户的购物车信息
     * @param cartToken
     * @param user
     * @return
     */
    @Override
    public List<Cart> getCartList(String cartToken, User user) {

        List<Cart> list = null;

        if (user != null) {
            //已经登录，购物车从数据库中获取
            QueryWrapper queryWrapper = new QueryWrapper();//注意这里是querywarpper,并不是warpper
            queryWrapper.eq("uid", user.getId());
            list = cartMapper.selectList(queryWrapper);
        }else {
            //未登录，购物车从redis获取
            if (cartToken != null) {
                //获得redis中购物车的长度
                Long size = redisTemplate.opsForList().size(cartToken);
                list = redisTemplate.opsForList().range(cartToken, 0, size);//含头含尾
            }
        }

        for (Cart cart : list) {

        }

        return null;
    }
}
