package com.example.springboot_demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class PageController {

    @GetMapping("/thymeleaf")
    public String thymeleaf(){
        return "thymeleafIndex";
    }

    @GetMapping("/freemarker")
    public String freemarker(){
        return "freemarkerIndex";
    }
}
