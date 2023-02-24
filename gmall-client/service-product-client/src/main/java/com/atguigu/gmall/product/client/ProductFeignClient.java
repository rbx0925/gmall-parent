package com.atguigu.gmall.product.client;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 23:42
 * @Description
 */

import com.atguigu.gmall.product.client.impl.ProductDegradeFeignClient;
import com.atguigu.gmall.product.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品微服务提供远程调用Feign client 远程调用商品微服务提供restful接口
 */
@FeignClient(value = "service-product", fallback = ProductDegradeFeignClient.class)
public interface ProductFeignClient {


    /*从目标微服务restful接口实现控制器复制*/


    /**
     * 根据SkuID查询SKU商品信息包含图片列表
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);


    /**
     * 通过三级分类id查询分类信息
     *
     * @param category3Id
     * @return
     */
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);


    /**
     * 根据商品实时最新价格
     *
     * @param skuId 商品ID
     * @return
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);



    /**
     * 根据商品SPUID查询商品海报列表
     *
     * @param spuId 商品SPUID
     * @return
     */
    @GetMapping("/api/product/inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable("spuId") Long spuId);


    /**
     * 根据SKUID查询当前SKU商品中平台属性以及属性值
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId);


    /**
     * 根据SPU_ID,SKU_ID查询所有销售属性，标识SKU选中销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId);


    /**
     * 返回所有销售属性对应skuID map集合
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);
}
