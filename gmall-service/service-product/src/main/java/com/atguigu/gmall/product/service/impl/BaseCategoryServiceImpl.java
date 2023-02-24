package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.model.BaseCategoryView;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 21:33
 * @Description
 */
@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    //根据三级分类id获取分类信息
    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }
}
