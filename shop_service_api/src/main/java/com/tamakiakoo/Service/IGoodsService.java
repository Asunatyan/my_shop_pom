package com.tamakiakoo.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tamakiakoo.entity.Goods;

import java.util.List;

public interface IGoodsService {

    List<Goods> getAll();

    int add(Goods goods);

    int update(Goods goods);

    int delete(int id);

    Goods getById(int goodId);

    public Page<Goods> getpage(Page<Goods> page);
}
