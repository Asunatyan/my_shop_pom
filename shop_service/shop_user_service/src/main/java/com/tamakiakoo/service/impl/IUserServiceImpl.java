package com.tamakiakoo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tamakiakoo.Service.IUserService;
import com.tamakiakoo.entity.User;
import com.tamakiakoo.mapper.IUserMapper;
import com.tamakiakoo.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private IUserMapper userMapper;

    @Override
    public User login(String username,String password) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", MD5Util.content2MD5(password));

        User user = userMapper.selectOne(queryWrapper);

        return user;
    }

    @Override
    public int register(User user) {

        //判断用户名是否已经注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User u= userMapper.selectOne(queryWrapper);
        if (u != null) {
            //用户名已存在
            return -1;
        }

        //判断邮箱是否已经注册
        QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("email", user.getEmail());
        User u2= userMapper.selectOne(queryWrapper2);
        if (u2 != null) {
            //邮箱已存在
            return -2;
        }

        //可以正常注册
        //对密码进行加密
        user.setPassword(MD5Util.content2MD5(user.getPassword()));

        return userMapper.insert(user);
    }

    @Override
    public User getUserByUsername(String username) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        QueryWrapper<User> wrapper= queryWrapper.eq("username", username);

        return userMapper.selectOne(wrapper);

    }

    @Override
    public int updatePassword(String username, String password) {

        User user = this.getUserByUsername(username);

        user.setPassword(MD5Util.content2MD5(password));

        return userMapper.updateById(user);
    }

}
