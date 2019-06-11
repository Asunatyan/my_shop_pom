package com.tamakiakoo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tamakiakoo.Service.IGoodsService;
import com.tamakiakoo.entity.Goods;
import com.tamakiakoo.utils.BaseResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private Configuration configuration;

    @Reference
    private IGoodsService goodsService;

    /**
     * 在添加商品的时候就应该身生成静态页面
     */

    @RequestMapping("/createHtml")
    public BaseResult createHtml(Integer gid) {
        //静态页面输出的路径 - 输出的静态页面必须能够让外界访问
        String path = ItemController.class.getResource("/static/html/").getPath() + gid + ".html";
        try (
                Writer out = new FileWriter(path)
        ){
            //获得商品详情页模板
            Template template = configuration.getTemplate("goods.ftl");

            Goods goods = goodsService.getById(gid);

            Map map = new HashMap();
            //获得商品的对应数据 - 调用该商品服务查询商品详细信息
            String[] gimages = goods.getGimages().split("\\|");
            map.put("goods", goods);
            map.put("gimages", gimages);

            //生成静态页
            template.process(map, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return BaseResult.success();
    }

}
