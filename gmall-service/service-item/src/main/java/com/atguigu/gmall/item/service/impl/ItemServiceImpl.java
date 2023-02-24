package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 23:50
 * @Description
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private ProductFeignClient productFeignClient;

    //根据skuID汇总sku商品详情页所有数据
    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        HashMap<String, Object> result = new HashMap<>();
        //1.根据skuId查询商品Sku信息包含商品图片 得到SkuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuId!=null){
            result.put("skuInfo", skuInfo);
        }
        //2.根据商品所属三级分类Id查询分类对象信息
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
        if (categoryView!=null){
            result.put("categoryView", categoryView);
        }
        //3.根据商品skuId查询商品价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        result.put("price", skuPrice);
        //4.根据spuId查询商品海报图片列表
        List<SpuPoster> spuPosterList = productFeignClient.findSpuPosterBySpuId(skuInfo.getSpuId());
        if (!CollectionUtils.isEmpty(spuPosterList)){
            result.put("spuPosterList", spuPosterList);
        }
        //5.根据skuId查询平台属性以及平台属性值
        List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
        if (!CollectionUtils.isEmpty(attrList)){
            List<Map<String, String>> skuAttrList = attrList.stream().map(baseAttrInfo -> {
                Map<String, String> attMap = new HashMap<>();
                String attrName = baseAttrInfo.getAttrName();
                String attrValue = baseAttrInfo.getAttrValueList().get(0).getValueName();
                attMap.put("attrName", attrName);
                attMap.put("attrValue", attrValue);
                return attMap;
            }).collect(Collectors.toList());
            result.put("skuAttrList", skuAttrList);
        }
        //6.根据spuID,skuID查询所有销售属性，以及当前sku选中销售属性
        List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
        if (!CollectionUtils.isEmpty(spuSaleAttrList)){
            result.put("spuSaleAttrList",spuSaleAttrList);
        }
        //7.根据spuID查询销售属性属性值对应skuId Map {"销售属性1|销售属性2":"skuId"}
        Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
        if (!CollectionUtils.isEmpty(skuValueIdsMap)){
            result.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
        }
        return result;
    }
}
