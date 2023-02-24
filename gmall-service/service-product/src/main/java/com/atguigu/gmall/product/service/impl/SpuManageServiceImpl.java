package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.base.model.BaseEntity;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.model.*;
import com.atguigu.gmall.product.service.SpuManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 20:14
 * @Description
 */
@Service
public class SpuManageServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuManageService {
    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuPosterMapper spuPosterMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public IPage<SpuInfo> getSpuInfoPage(IPage<SpuInfo> ipage, Long category3Id) {
        LambdaQueryWrapper<SpuInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (category3Id != null) {
            queryWrapper.eq(SpuInfo::getCategory3Id, category3Id);
        }
        queryWrapper.eq(BaseEntity::getIsDeleted,"0");
        //根据修改时间进行倒序
        queryWrapper.orderByDesc(SpuInfo::getUpdateTime);
        return spuInfoMapper.selectPage(ipage,queryWrapper);
    }

    //获取销售属性数据
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    //保存spu
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存到spu_info表中
        spuInfoMapper.insert(spuInfo);
        //保存商品spu_image图片表
        if (!CollectionUtils.isEmpty(spuInfo.getSpuImageList())) {
            for (SpuImage spuImage : spuInfo.getSpuImageList()) {
                //将图片关联到商品SPU
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }
        //保存商品海报图片到spu_poster表 关联到商品spu
        if (!CollectionUtils.isEmpty(spuInfo.getSpuPosterList())){
            for (SpuPoster spuPoster : spuInfo.getSpuPosterList()){
                //将海报图片关联到商品SPU
                spuPoster.setSpuId(spuInfo.getId());
                spuPosterMapper.insert(spuPoster);
            }
        }
        //保存商品Spu对应的销售属性名称到spu_sale_attr表中 。。
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //保存商品Spu对应销售属性值表到spu_sale_attr_value表 。。
                if (!CollectionUtils.isEmpty(spuSaleAttr.getSpuSaleAttrValueList())){
                    for (SpuSaleAttrValue spuSaleAttrValue:spuSaleAttr.getSpuSaleAttrValueList()){
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    //根据spuId 查询销售属性
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        LambdaQueryWrapper<SpuSaleAttr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuSaleAttr::getSpuId,spuId);
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        List<SpuSaleAttr> list = spuSaleAttrMapper.selectList(wrapper);
        for (SpuSaleAttr spuSaleAttr : list) {
            LambdaQueryWrapper<SpuSaleAttrValue> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SpuSaleAttrValue::getSpuId,spuId);
            queryWrapper.eq(SpuSaleAttrValue::getBaseSaleAttrId,spuSaleAttr.getBaseSaleAttrId());
            queryWrapper.eq(BaseEntity::getIsDeleted,"0");
            List<SpuSaleAttrValue> valueList = spuSaleAttrValueMapper.selectList(queryWrapper);
            spuSaleAttr.setSpuSaleAttrValueList(valueList);
        }
        return list;
    }

    //根据spuId 获取spuImage集合
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        LambdaQueryWrapper<SpuImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuImage::getSpuId,spuId);
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        return spuImageMapper.selectList(wrapper);
    }

    //根据spuId 获取海报数据
    @Override
    @GmallCache(prefix = "spuPosterBySpuId:")
    public List<SpuPoster> findSpuPosterBySpuId(Long spuId) {
        LambdaQueryWrapper<SpuPoster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuPoster::getSpuId,spuId);
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        return spuPosterMapper.selectList(wrapper);
    }

    //根据spuId,skuId 获取销售属性数据
    @Override
    @GmallCache(prefix = "SpuSaleAttrListCheckBySku:")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }
}
