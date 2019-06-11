package com.tamakiakoo.Service;

import com.tamakiakoo.entity.Cart;
import com.tamakiakoo.entity.User;

import java.util.List;

public interface ICartService {
    int addCart(Cart cart);

    List<Cart> getCartList(String cartToken, User user);

}
