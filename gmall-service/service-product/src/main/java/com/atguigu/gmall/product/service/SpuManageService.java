package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseSaleAttr;
import com.atguigu.gmall.product.model.SpuInfo;
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
}
