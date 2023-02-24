package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseAttrInfo;
import com.atguigu.gmall.product.model.SkuInfo;
import com.atguigu.gmall.product.model.SpuPoster;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 15:21
 * @Description
 */
public interface SkuManageService {
    //保存SkuInfo
    void saveSkuInfo(SkuInfo skuInfo);

    //商品管理 -> sku分页列表
    IPage<SkuInfo> getSkuInfoPage(IPage<SkuInfo> iPage, Long category3Id);

    //商品管理 -> 上架
    void onSale(Long skuId);

    //商品管理 -> 下架
    void cancelSale(Long skuId);

    //根据skuId获取SkuInfo
    SkuInfo getSkuInfo(Long skuId);

    //根据skuId 获取最新的商品价格
    BigDecimal getSkuPrice(Long skuId);

    //根据skuId 获取平台属性数据
    List<BaseAttrInfo> getAttrList(Long skuId);

    //根据spuId 获取到销售属性值Id 与skuId 组成的数据集
    Map getSkuValueIdsMap(Long spuId);
}
