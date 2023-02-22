package com.atguigu.gmall.product.service;


import com.atguigu.gmall.product.model.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 18:56
 * @Description
 */
public interface BaseCategory2Service extends IService<BaseCategory2> {
    //获取二级分类数据
    List<BaseCategory2> getCategory2ById(Long category1Id);
}
