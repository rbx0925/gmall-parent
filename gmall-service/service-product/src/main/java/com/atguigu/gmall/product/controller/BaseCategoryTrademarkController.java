package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.model.BaseTrademark;
import com.atguigu.gmall.product.model.CategoryTrademarkVo;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 17:38
 * @Description
 */
@Api(tags = "分类品牌中间表控制器")
@RestController
@RequestMapping("/admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {
    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;

    //根据category3Id获取品牌列表
    @GetMapping("/findTrademarkList/{category3Id}")
    public Result findTrademarkList(@PathVariable("category3Id") Long category3Id){
        List<BaseTrademark> list = baseCategoryTrademarkService.findTrademarkListBy3Id(category3Id);
        return Result.ok(list);
    }

    //根据category3Id获取可选品牌列表
    @GetMapping("/findCurrentTrademarkList/{category3Id}")
    public Result<List<BaseTrademark>> findCurrentTrademarkList(@PathVariable("category3Id") Long category3Id){
        List<BaseTrademark> list = baseCategoryTrademarkService.findCurrentTrademarkList(category3Id);
        return Result.ok(list);
    }

    //保存分类品牌关联
    @PostMapping("/save")
    public Result saveBasecategoryTrademark(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        baseCategoryTrademarkService.saveBasecategoryTrademark(categoryTrademarkVo);
        return Result.ok();
    }

    //删除分类品牌关联
    @DeleteMapping("/remove/{category3Id}/{trademarkId}")
    public Result removeCategoryTrademark(@PathVariable("category3Id") Long category3Id, @PathVariable("trademarkId")Long trademarkId){
        baseCategoryTrademarkService.removeCategoryTrademark(category3Id,trademarkId);
        return Result.ok();
    }
}
