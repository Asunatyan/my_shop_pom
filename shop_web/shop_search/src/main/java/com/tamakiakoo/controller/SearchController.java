package com.tamakiakoo.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.tamakiakoo.entity.Goods;
import com.tamakiakoo.Service.ISearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Reference
    private ISearchService searchService;


    @RequestMapping("/getByKeyWord")
    public String getByKeyWord(String keyword, Model model){

        List<Goods> goodsList = searchService.getByKeyWord(keyword);

        model.addAttribute("goodsList", goodsList);

        return "searchlist";
    }

}
