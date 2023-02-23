package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.base.model.BaseEntity;
import com.atguigu.gmall.product.model.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 品牌表 业务实现类
 *
 * @author atguigu
 * @since 2023-02-22
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService {

    //品牌分页列表查询
    @Override
    public IPage<BaseTrademark> baseTrademarkByPage(IPage<BaseTrademark> iPage) {
        LambdaQueryWrapper<BaseTrademark> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BaseEntity::getUpdateTime);
        return this.page(iPage,wrapper);
    }
}
