package com.tamakiakoo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/back")
public class BackController {


    @RequestMapping("/{page}")
    public String topage(@PathVariable("page") String page){
        return page;
    }
}
