package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.model.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * spu销售属性 Mapper 接口
 *
 * @author atguigu
 * @since 2023-02-23
 */
@Repository
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    //根据spuId,skuId 获取销售属性数据
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);
}
