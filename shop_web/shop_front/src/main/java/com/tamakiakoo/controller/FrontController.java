package com.tamakiakoo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/front")
public class FrontController {

    @RequestMapping("/{page}")
    public String page(@PathVariable("page")String page){
        return page;
    }

}
