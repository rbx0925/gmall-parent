package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.model.BaseSaleAttr;
import com.atguigu.gmall.product.model.SpuImage;
import com.atguigu.gmall.product.model.SpuInfo;
import com.atguigu.gmall.product.model.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 19:51
 * @Description
 */
@RestController
@RequestMapping("/admin/product")
public class SpuManageController {
    @Autowired
    private SpuManageService spuManageService;

    //spu分页列表
    @GetMapping("/{page}/{size}")
    public Result getSpuInfoPage(@PathVariable("page") Long page, @PathVariable("size")Long size, @RequestParam Long category3Id) {
        IPage<SpuInfo> ipage = new Page<>(page, size);
        ipage = spuManageService.getSpuInfoPage(ipage,category3Id);
        return Result.ok(ipage);
    }

    //获取销售属性数据
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> list = spuManageService.getBaseSaleAttrList();
        return Result.ok(list);
    }

    //保存spu
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuManageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    //根据spuId 查询销售属性
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list = spuManageService.spuSaleAttrList(spuId);
        return Result.ok(list);
    }

    //根据spuId 获取spuImage 集合
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){
        List<SpuImage> list = spuManageService.spuImageList(spuId);
        return Result.ok(list);
    }
}
