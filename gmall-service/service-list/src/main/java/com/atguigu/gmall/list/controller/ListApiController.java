package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.model.Goods;
import com.atguigu.gmall.list.model.SearchParam;
import com.atguigu.gmall.list.model.SearchResponseVo;
import com.atguigu.gmall.list.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author rbx
 * @title
 * @Create 2023-03-03 16:51
 * @Description
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {
    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;
    @Autowired
    private SearchService searchService;

    //商品搜索
    @GetMapping("inner/createIndex")
    public Result createIndex(){
        esRestTemplate.createIndex(Goods.class);
        esRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    //测试接口，商品文档对象录入索引
    @GetMapping("/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId")Long skuId){
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    //测试接口，商品文档删除
    @GetMapping("/inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId")Long skuId){
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    //更新商品的热度排名分值
    @GetMapping("/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId")Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    //商品检索
    @PostMapping("/inner")
    public Result search(@RequestBody SearchParam searchParam){
        SearchResponseVo responseVo = searchService.search(searchParam);
        return Result.ok(responseVo);
    }

}
