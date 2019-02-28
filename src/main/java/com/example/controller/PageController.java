package com.example.controller;

import com.example.entity.UserInfo;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/index")
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/thymeleaf")
    public String thymeleaf(){
        return "thymeleafIndex";
    }

    @GetMapping("/freemarker")
    public String freemarker(){
        return "freemarkerIndex";
    }

    @GetMapping("/user")
    public ModelAndView toUserList(){
        List<UserInfo> userInfoList = userService.getUserList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("thymeleafIndex");
        modelAndView.addObject("userList",userInfoList);
        return modelAndView;
    }
}
