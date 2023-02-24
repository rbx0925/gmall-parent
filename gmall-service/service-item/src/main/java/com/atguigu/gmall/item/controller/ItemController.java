package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 23:48
 * @Description
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 根据skuID汇总sku商品详情页所有数据
     *
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}")
    public Result<Map<String, Object>> getBySkuId(@PathVariable("skuId") Long skuId) {
        Map<String, Object> result = itemService.getBySkuId(skuId);
        return Result.ok(result);
    }
}
