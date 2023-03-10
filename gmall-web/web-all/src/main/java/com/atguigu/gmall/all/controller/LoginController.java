package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author rbx
 * @title
 * @Create 2023-03-07 18:58
 * @Description
 */
@Controller
public class LoginController {
    /**
     * 渲染登录页面
     *
     * @param originUrl
     * @return
     */
    @GetMapping("/login.html")
    public String loginHtml(@RequestParam(value = "originUrl", required = false) String originUrl, Model model) {
        //给页面中用于调整登录前页面进行赋值
        model.addAttribute("originUrl", originUrl);
        return "/login";
    }
}
