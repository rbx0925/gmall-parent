package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.base.model.BaseEntity;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.model.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 18:39
 * @Description
 */
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategory1Service {

    @Override
    public List<BaseCategory1> getCategory1() {
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        return this.list(wrapper);
    }
}
