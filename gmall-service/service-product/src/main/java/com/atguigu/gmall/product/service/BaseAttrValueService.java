package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 平台属性值表 业务接口类
 * @author atguigu
 * @since 2023-02-22
 */
public interface BaseAttrValueService extends IService<BaseAttrValue> {

    //根据平台属性Id 获取到平台属性值集合
    List<BaseAttrValue> getAttrValueList(Long attrId);
}
