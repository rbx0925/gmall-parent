package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseCategoryTrademark;
import com.atguigu.gmall.product.model.BaseTrademark;
import com.atguigu.gmall.product.model.CategoryTrademarkVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分类品牌中间表 业务接口类
 * @author atguigu
 * @since 2023-02-22
 */
public interface BaseCategoryTrademarkService extends IService<BaseCategoryTrademark> {

    //根据category3Id获取品牌列表
    List<BaseTrademark> findTrademarkListBy3Id(Long category3Id);

    //根据category3Id获取可选品牌列表
    List<BaseTrademark> findCurrentTrademarkList(Long category3Id);

    //保存分类品牌关联
    void saveBasecategoryTrademark(CategoryTrademarkVo categoryTrademarkVo);

    //删除分类品牌关联
    void removeCategoryTrademark(Long category3Id, Long trademarkId);
}
