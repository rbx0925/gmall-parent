package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 19:55
 * @Description
 */
public interface SpuManageService extends IService<SpuInfo> {

    //spu分页列表
    IPage<SpuInfo> getSpuInfoPage(IPage<SpuInfo> ipage, Long category3Id);

    //获取销售属性数据
    List<BaseSaleAttr> getBaseSaleAttrList();

    //保存spu
    void saveSpuInfo(SpuInfo spuInfo);

    //根据spuId 查询销售属性
    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    //根据spuId 获取spuImage 集合
    List<SpuImage> spuImageList(Long spuId);

    //根据spuId 获取海报数据
    List<SpuPoster> findSpuPosterBySpuId(Long spuId);

    //根据spuId,skuId 获取销售属性数据
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);
}
