package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.product.model.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 19:48
 * @Description
 */
@Repository
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    //根据分类Id 获取平台属性集合
    List<BaseAttrInfo> getAttrInfoList(@Param("category1Id") Long category1Id, @Param("category2Id") Long category2Id, @Param("category3Id") Long category3Id);

    //根据skuId 获取平台属性数据
    List<BaseAttrInfo> getAttrList(@Param("skuId") Long skuId);
}
