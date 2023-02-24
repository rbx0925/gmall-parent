package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-03-02 15:14
 * @Description
 */
@Controller
public class IndexController {

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 渲染首页
     * @param model
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        //1.远程获取分类数据
        List<JSONObject> list = productFeignClient.getBaseCategoryList();

        //2.添加数据到模型对象Model
        model.addAttribute("list", list);

        //3.返回模板页面
        return "/index/index.html";
    }
}
