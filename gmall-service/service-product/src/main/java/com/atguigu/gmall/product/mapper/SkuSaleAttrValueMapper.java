package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.model.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性值 Mapper 接口
 *
 * @author atguigu
 * @since 2023-02-23
 */
@Repository
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    //根据spuId 获取到销售属性值Id 与skuId 组成的数据集
    List<Map> selectSaleAttrValuesBySpu(Long spuId);
}
