package com.atguigu.gmall.product.service;


import com.atguigu.gmall.product.model.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 19:00
 * @Description
 */
public interface BaseCategory3Service extends IService<BaseCategory3> {
    //获取三级分类数据
    List<BaseCategory3> getCategory3ById(Long category2Id);
}
