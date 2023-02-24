package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 23:49
 * @Description
 */
public interface ItemService {
    //根据skuID汇总sku商品详情页所有数据
    Map<String, Object> getBySkuId(Long skuId);
}
