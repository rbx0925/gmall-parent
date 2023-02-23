package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 品牌表 业务接口类
 * @author atguigu
 * @since 2023-02-22
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {


    //品牌分页列表查询
    IPage<BaseTrademark> baseTrademarkByPage(IPage<BaseTrademark> iPage);

}
