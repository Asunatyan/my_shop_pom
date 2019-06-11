package com.tamakiakoo.Service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tamakiakoo.Service.IGoodsService;
import com.tamakiakoo.Service.ISearchService;
import com.tamakiakoo.entity.Goods;
import com.tamakiakoo.mapper.IGoodsMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class GoodeServiceImpl implements IGoodsService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Reference
    private ISearchService searchService;

    @Autowired
    private IGoodsMapper goodsMapper;

    @Override
    public List<Goods> getAll() {
        return goodsMapper.selectList(null);
    }

    @Override
    public int add(Goods goods) {
        int insert = goodsMapper.insert(goods);
        //放在插入的后面主键回填,因为solr字段必须设置一个id正好主键符合

        //同步到solr索引库里面
        //int i = searchService.addGoods(goods);
        //通过http通知详情工程生成静态页面
        //HttpUtil.sendGet("http://localhost:8083/item/createHtml?gid=" + goods.getId());
        //现在使用rabbitmq实现上面的代码
        rabbitTemplate.convertAndSend("goods_exchange","", goods);
        return 1;
    }

    @Override
    public int update(Goods goods) {
        goodsMapper.updateById(goods);
        int i = searchService.updateGoods(goods);
        return i;
    }

    @Override
    public int delete(int id) {
        goodsMapper.deleteById(id);
        int i = searchService.deleteGoods(id);
        return i;
    }

    @Override
    public Goods getById(int goodId) {
        return goodsMapper.selectById(goodId);
    }


    public Page<Goods> getpage(Page<Goods> page) {

        return (Page<Goods>) goodsMapper.selectPage(page, null);
    }




}
