package com.atguigu.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.model.*;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.SkuManageService;
import com.atguigu.gmall.product.service.SpuManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 20:57
 * @Description
 */
@RestController
@RequestMapping("/api/product")
public class ProductApiController {
    @Autowired
    private SkuManageService skuManageService;
    @Autowired
    private SpuManageService spuManageService;
    @Autowired
    private BaseCategoryService baseCategoryService;
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    //根据skuId获取SkuInfo
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        return skuManageService.getSkuInfo(skuId);
    }

    //根据三级分类id获取分类信息
    @GetMapping("/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        return baseCategoryService.getCategoryView(category3Id);
    }

    //根据skuId 获取最新的商品价格
    @GetMapping("/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId")Long skuId){
        return skuManageService.getSkuPrice(skuId);
    }

    //根据spuId 获取海报数据
    @GetMapping("/inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable("spuId")Long spuId){
        return spuManageService.findSpuPosterBySpuId(spuId);
    }

    //根据skuId 获取平台属性数据
    @GetMapping("/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId")Long skuId){
        return skuManageService.getAttrList(skuId);
    }

    //根据spuId,skuId 获取销售属性数据
    @GetMapping("/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId")Long  spuId){
        return spuManageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    //根据spuId 获取到销售属性值Id 与skuId 组成的数据集
    @GetMapping("/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        return skuManageService.getSkuValueIdsMap(spuId);
    }

    @GetMapping("/inner/getBaseCategoryList")
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list = baseCategoryService.getBaseCategoryList();
        return list;
    }

    //根据品牌Id 获取品牌数据
    @GetMapping("/inner/getTrademark/{tmId}")
    public BaseTrademark getTrademarkById(@PathVariable("tmId")Long tmId){
        return baseTrademarkService.getById(tmId);
    }
}
