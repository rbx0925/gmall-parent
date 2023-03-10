package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 23:59
 * @Description
 */
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    /**
     * sku详情页面
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model) {
        // 通过skuId 查询skuInfo
        Result<Map> result = itemFeignClient.getItem(skuId);
        model.addAllAttributes(result.getData());
        return "item/item";
    }


}
