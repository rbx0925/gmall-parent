package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.model.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 19:47
 * @Description
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {
    //根据分类Id 获取平台属性集合
    List<BaseAttrInfo> selectBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id);


    //保存-修改平台属性
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
