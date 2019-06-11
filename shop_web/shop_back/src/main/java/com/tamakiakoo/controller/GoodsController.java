package com.tamakiakoo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tamakiakoo.entity.Goods;
import com.tamakiakoo.utils.BaseResult;
import com.tamakiakoo.Service.IGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {


    public static Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Reference
    private IGoodsService goodsService;

    @Value("${imgs.service}")
    private String imgPath;

    /**
     * 获取所有Goods
     * @param model
     * @return
     */
    @RequestMapping(value = "/getAll")
    public String getAll(Model model){
        List<Goods> goodsList = goodsService.getAll();

        model.addAttribute("imgPath", imgPath);

        model.addAttribute("goodsList", goodsList);
        return "goodsList";
    }

    /**
     * 分页
     * @param model
     * @return
     */
    @RequestMapping(value = "/getPage")
    public String getPage(Page<Goods> page,Model model){
        page.setSize(5);
        Page<Goods> page2 = goodsService.getpage(page);
        model.addAttribute("page", page2);
        model.addAttribute("imgPath", imgPath);
        model.addAttribute("url", "goods/getPage?");


        return "goodsList2";
    }
 @RequestMapping(value = "/getPage2")
    public String getPage2(Page<Goods> page,ModelMap map){
        page.setSize(5);
        Page<Goods> page2 = goodsService.getpage(page);
        map.put("page", page2);
        map.put("imgPath", imgPath);


        map.put("url", "goods/getPage?key=123");
        return "goodsList2";
    }

    /**
     * 根据id删除商品
     * @param goodId
     * @return
     */
    /*produces = "text/html;charset=utf-8"  对象返回是一个字符串,并不是一个json的数据 所以前台不能对象,属性*/
    @RequestMapping(value = "/delete/{goodId}", produces="application/json;charset=UTF-8")
    @ResponseBody
    public BaseResult delete(@PathVariable("goodId") int goodId){

        int delete = goodsService.delete(goodId);

        return BaseResult.success("删除成功~~~");

    }

    /**
     * 跳转到更新页面
     * @param goodId
     * @param model
     * @return
     */
    @RequestMapping(value = "/toUpdate/{goodId}")
    public String toUpdate(@PathVariable("goodId") int goodId ,Model model){

        Goods goods = goodsService.getById(goodId);

        model.addAttribute("imgPath", imgPath);
        model.addAttribute("goods", goods);
        return "goodsupdate";

    }

    /**
     * 添加
     * @param goods
     * @param imgList
     * @return
     */
    @RequestMapping("/add")
    public String add(Goods goods,String[] imgList){


        //System.out.println(goods);

        String gimages = "";
        if (imgList!= null && imgList.length>0) {
            for (String s : imgList) {
                gimages += s + "|";
            }
        }

        goods.setGimages(gimages);

        logger.info("gimages is {}",gimages);

        int result = goodsService.add(goods);

        return "redirect:getAll";
    }

    /**
     * 修改
     * @param goods
     * @param imgList
     * @return
     */
    @RequestMapping(value = "/update", produces="application/json;charset=UTF-8")
    @ResponseBody
    public BaseResult update(Goods goods,@RequestParam("images[]") String[] imgList){


        //System.out.println(goods);

        String gimages = "";
        if (imgList!= null && imgList.length>0) {
            for (String s : imgList) {
                gimages += s + "|";
            }
        }
        goods.setGimages(gimages);


        int result = goodsService.update(goods);

        return BaseResult.success("修改成功");
    }





}
