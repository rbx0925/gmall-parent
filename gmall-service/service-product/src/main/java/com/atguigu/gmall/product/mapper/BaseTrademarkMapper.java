package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.model.BaseTrademark;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 品牌表 Mapper 接口
 *
 * @author atguigu
 * @since 2023-02-22
 */
public interface BaseTrademarkMapper extends BaseMapper<BaseTrademark> {

    @Select("SELECT * from base_trademark where id IN(SELECT DISTINCT trademark_id from base_category_trademark where base_category_trademark.category3_id=#{category3Id} AND is_deleted=0) and is_deleted=0")
    List<BaseTrademark> findTrademarkListBy3Id(@Param("category3Id") Long category3Id);

    @Select("SELECT * from base_trademark where id NOT IN(SELECT DISTINCT trademark_id from base_category_trademark where base_category_trademark.category3_id=#{category3Id} AND is_deleted=0) and is_deleted=0")
    List<BaseTrademark> findCurrentTrademarkList(@Param("category3Id") Long category3Id);
}
