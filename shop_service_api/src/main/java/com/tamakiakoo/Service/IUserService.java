package com.tamakiakoo.Service;

import com.tamakiakoo.entity.User;

public interface IUserService {

    public User login(String username,String password);

    public int register(User user);


    User getUserByUsername(String username);

    int updatePassword(String username, String password);
}
