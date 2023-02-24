package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.model.*;
import com.atguigu.gmall.product.service.SkuManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 14:24
 * @Description
 */
@Api(tags = "商品SKU控制器")
@RestController
@RequestMapping("/admin/product")
public class SkuManageController {

    @Autowired
    private SkuManageService skuManageService;

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuManageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //商品管理 -> sku分页列表
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuInfoPage(@PathVariable("page") Long page, @PathVariable("limit") Long limit,@RequestParam(value = "category3Id")Long category3Id){
        IPage<SkuInfo> iPage = new Page<>(page,limit);
        iPage  = skuManageService.getSkuInfoPage(iPage,category3Id);
        return Result.ok(iPage);
    }

    //商品管理 -> 上架
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuManageService.onSale(skuId);
        return Result.ok();
    }

    //商品管理 -> 下架
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuManageService.cancelSale(skuId);
        return Result.ok();
    }


}
