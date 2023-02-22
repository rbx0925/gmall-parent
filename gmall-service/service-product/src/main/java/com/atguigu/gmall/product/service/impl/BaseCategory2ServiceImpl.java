package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.base.model.BaseEntity;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.model.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 18:56
 * @Description
 */
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2> implements BaseCategory2Service {
    //获取二级分类数据
    @Override
    public List<BaseCategory2> getCategory2ById(Long category1Id) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        wrapper.eq(BaseCategory2::getCategory1Id,category1Id);
        return this.list(wrapper);
    }
}
