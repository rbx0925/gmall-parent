package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.model.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 16:51
 * @Description
 */
@Api(tags = "品牌表控制器")
@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    //品牌分页列表查询
    @GetMapping("/{page}/{limit}")
    public Result baseTrademarkByPage(
            @PathVariable(value = "page")Long page,
            @PathVariable(value = "limit")Long limit
    ){

        IPage<BaseTrademark> iPage = new Page<>(page, limit);
        iPage = baseTrademarkService.baseTrademarkByPage(iPage);
        return Result.ok(iPage);
    }

    //品牌管理 -> 保存
    @PostMapping("/save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    //品牌管理 -> 更新
    @PutMapping("/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    //品牌管理 -> 根据品牌ID查询品牌信息
    @RequestMapping("/get/{id}")
    public Result getById(@PathVariable("id")Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    //品牌管理 -> 删除
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable("id")Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

}
