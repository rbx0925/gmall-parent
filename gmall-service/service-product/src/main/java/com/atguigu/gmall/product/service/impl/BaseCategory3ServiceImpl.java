package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.base.model.BaseEntity;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.model.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 19:01
 * @Description
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3> implements BaseCategory3Service {
    //获取三级分类数据
    @Override
    public List<BaseCategory3> getCategory3ById(Long category2Id) {
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        wrapper.eq(BaseCategory3::getCategory2Id,category2Id);
        return this.list(wrapper);
    }
}
