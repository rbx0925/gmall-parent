package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.product.model.*;
import com.atguigu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 18:34
 * @Description
 */
@RestController
@RequestMapping("/admin/product")
public class BaseCategoryApiController {
    @Autowired
    private BaseCategory1Service baseCategory1Service;

    @Autowired
    private BaseCategory2Service baseCategory2Service;

    @Autowired
    private BaseCategory3Service baseCategory3Service;

    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    @Autowired
    private BaseAttrValueService baseAttrValueService;
    //获取一级分类数据
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> list =  baseCategory1Service.getCategory1();
        return Result.ok(list);
    }

    //获取二级分类数据
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable(value = "category1Id") Long category1Id){
        List<BaseCategory2> list = baseCategory2Service.getCategory2ById(category1Id);
        return Result.ok(list);
    }

    //获取三级分类数据
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable(value = "category2Id") Long category2Id){
        List<BaseCategory3> list = baseCategory3Service.getCategory3ById(category2Id);
        return Result.ok(list);
    }

    //根据分类Id 获取平台属性集合
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable(value = "category1Id")Long category1Id, @PathVariable(value = "category2Id")Long category2Id, @PathVariable(value = "category3Id")Long category3Id){
        List<BaseAttrInfo> list = baseAttrInfoService.selectBaseAttrInfoList(category1Id, category2Id,category3Id);
        return Result.ok(list);
    }

    //保存-修改平台属性
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    //根据平台属性Id 获取到平台属性值集合
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> list = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(list);
    }
}
