package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;

import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.model.BaseAttrInfo;
import com.atguigu.gmall.product.model.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 19:48
 * @Description
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo> implements BaseAttrInfoService {
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;


    //根据分类Id 获取平台属性集合
    @Override
    public List<BaseAttrInfo> selectBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return this.getBaseMapper().getAttrInfoList(category1Id, category2Id, category3Id);
    }

    //保存-修改平台属性
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() == null){
            //新增
            baseAttrInfoMapper.insert(baseAttrInfo);
        }else {
            //更新
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }

        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseAttrValue::getAttrId,baseAttrInfo.getId());
        baseAttrValueMapper.delete(wrapper);

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(attrValueList)){
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }
}
