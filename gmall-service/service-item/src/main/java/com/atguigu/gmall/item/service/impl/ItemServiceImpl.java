package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.item.config.ThreadPoolConfig;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.product.model.*;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
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
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private ListFeignClient listFeignClient;

    //根据skuID汇总sku商品详情页所有数据
    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        HashMap<String, Object> result = new HashMap<>();
        //判断用户要查询的商品是否不存在,如果不存在直接返回null
        /*RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        if (!bloomFilter.contains(skuId)) {
            return result;
        }*/
        //1.根据skuId查询商品Sku信息包含商品图片 得到SkuInfo
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuId != null) {
                result.put("skuInfo", skuInfo);
            }
            return skuInfo;
        }, threadPoolExecutor);

        //2.根据商品所属三级分类Id查询分类对象信息
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
            if (categoryView != null) {
                result.put("categoryView", categoryView);
            }
        }, threadPoolExecutor);

        //3.根据商品skuId查询商品价格
        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            result.put("price", skuPrice);
        }, threadPoolExecutor);

        //4.根据spuId查询商品海报图片列表
        CompletableFuture<Void> spuPosterListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuPoster> spuPosterList = productFeignClient.findSpuPosterBySpuId(skuInfo.getSpuId());
            if (!CollectionUtils.isEmpty(spuPosterList)) {
                result.put("spuPosterList", spuPosterList);
            }
        }, threadPoolExecutor);

        //5.根据skuId查询平台属性以及平台属性值
        CompletableFuture<Void> attrListCompletableFuture = CompletableFuture.runAsync(() -> {
            List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
            if (!CollectionUtils.isEmpty(attrList)) {
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
        }, threadPoolExecutor);

        //6.根据spuID,skuID查询所有销售属性，以及当前sku选中销售属性
        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
                result.put("spuSaleAttrList", spuSaleAttrList);
            }
        }, threadPoolExecutor);

        //7.根据spuID查询销售属性属性值对应skuId Map {"销售属性1|销售属性2":"skuId"}
        CompletableFuture<Void> valuesSkuJsonCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            if (!CollectionUtils.isEmpty(skuValueIdsMap)) {
                result.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
            }
        }, threadPoolExecutor);

        //8.远程调用搜索微服务，更新ES索引库中商品文档热门分值
        CompletableFuture<Void> incrHotScoreCompletableFuture = CompletableFuture.runAsync(() -> {
            listFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);

        //8.将以上七个任务全部并行执行，执行完所有任务才返回
        CompletableFuture.allOf(skuInfoCompletableFuture,
                categoryViewCompletableFuture,
                priceCompletableFuture,
                spuPosterListCompletableFuture,
                attrListCompletableFuture,
                spuSaleAttrListCompletableFuture,
                valuesSkuJsonCompletableFuture,
                incrHotScoreCompletableFuture).join();
        return result;
    }
}
