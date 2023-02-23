package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.model.BaseCategoryTrademark;
import com.atguigu.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.atguigu.gmall.product.model.BaseTrademark;
import com.atguigu.gmall.product.model.CategoryTrademarkVo;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * 分类品牌中间表 业务实现类
 *
 * @author atguigu
 * @since 2023-02-22
 */
@Service
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;

    //根据category3Id获取品牌列表
    @Override
    public List<BaseTrademark> findTrademarkListBy3Id(Long category3Id) {
        List<BaseTrademark> list = baseTrademarkMapper.findTrademarkListBy3Id(category3Id);
        return list;
    }

    //根据category3Id获取可选品牌列表
    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id) {
        return baseTrademarkMapper.findCurrentTrademarkList(category3Id);
    }

    //保存分类品牌关联
    @Override
    public void saveBasecategoryTrademark(CategoryTrademarkVo categoryTrademarkVo) {
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        trademarkIdList.stream().forEach(aLong -> {
            BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
            baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
            baseCategoryTrademark.setTrademarkId(aLong);
            baseCategoryTrademarkMapper.insert(baseCategoryTrademark);
        });
    }

    //删除分类品牌关联
    @Override
    public void removeCategoryTrademark(Long category3Id, Long trademarkId) {
        LambdaQueryWrapper<BaseCategoryTrademark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategoryTrademark::getCategory3Id,category3Id);
        wrapper.eq(BaseCategoryTrademark::getTrademarkId,trademarkId);
        baseCategoryTrademarkMapper.delete(wrapper);
    }

}
