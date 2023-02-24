package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseCategoryView;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 21:33
 * @Description
 */
public interface BaseCategoryService {
    //根据三级分类id获取分类信息
    BaseCategoryView getCategoryView(Long category3Id);
}
