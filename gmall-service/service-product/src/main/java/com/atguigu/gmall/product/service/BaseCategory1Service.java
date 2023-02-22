package com.atguigu.gmall.product.service;


import com.atguigu.gmall.product.model.BaseCategory1;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 18:38
 * @Description
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {
    //获取一级分类数据
    List<BaseCategory1> getCategory1();
}
