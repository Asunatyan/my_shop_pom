package com.tamakiakoo.Service;

import com.tamakiakoo.entity.Goods;

import java.util.List;

public interface ISearchService {

    public List<Goods> getByKeyWord(String keyword);

    public int addGoods(Goods goods);

    int deleteGoods(int id);

    int updateGoods(Goods goods);
}
