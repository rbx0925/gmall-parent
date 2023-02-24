package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.base.model.BaseEntity;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.model.*;
import com.atguigu.gmall.product.service.SkuManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 15:21
 * @Description
 */
@Service
public class SkuManageServiceImpl implements SkuManageService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 保存SKU信息
     *      * 1.将SKU基本信息存入sku_info表中
     *      * 2.将提交SKU图片存入sku_image表 关联SKU  设置sku_id逻辑外键
     *      * 3.将提交的平台属性列表 批量保存 sku_attr_value  关联SKU  设置sku_id逻辑外键
     *      * 4.将提交的销售属性列表 批量保存 sku_sale_attr_value  关联SKU  设置sku_id逻辑外键
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1.将SKU基本信息存入sku_info表中
        skuInfoMapper.insert(skuInfo);
        //2.将提交SKU图片存入sku_image表 关联SKU  设置sku_id逻辑外键
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }
        //3.将提交的平台属性列表 批量保存 sku_attr_value  关联SKU  设置sku_id逻辑外键
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //4.将提交的销售属性列表 批量保存 sku_sale_attr_value  关联SKU  设置sku_id逻辑外键
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSkuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        //5.将保存的商品SKU_ID存放到布隆过滤器
       /* RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        bloomFilter.add(skuInfo.getId());*/
    }

    //商品管理 -> sku分页列表
    @Override
    public IPage<SkuInfo> getSkuInfoPage(IPage<SkuInfo> iPage, Long category3Id) {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(category3Id)) {
            wrapper.eq(SkuInfo::getCategory3Id,category3Id);
        }
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        wrapper.orderByDesc(BaseEntity::getUpdateTime);
        return skuInfoMapper.selectPage(iPage,wrapper);
    }

    //商品管理 -> 上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    //商品管理 -> 下架
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        LambdaUpdateWrapper<SkuInfo> updateWrapper = new LambdaUpdateWrapper<>();
        //设置更新字段
        updateWrapper.set(SkuInfo::getIsSale, 0);
        //设置更新条件
        updateWrapper.eq(SkuInfo::getId, skuId);
        skuInfoMapper.update(skuInfo, updateWrapper);
    }

    //根据skuId获取SkuInfo


    /*public SkuInfo getSkuInfo(Long skuId) {
        String dataKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(dataKey);
        if (skuInfo == null) {
            String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
            RLock lock = redissonClient.getLock(lockKey);
            try {
                boolean flag = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (flag) {
                    skuInfo = this.getSkuInfoFromDB(skuId);
                    if (skuInfo == null) {
                        redisTemplate.opsForValue().set(dataKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.MILLISECONDS);
                        return skuInfo;
                    }else {
                        redisTemplate.opsForValue().set(dataKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                    }
                }else {
                    return this.getSkuInfo(skuId);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
        }else {
            return skuInfo;
        }
        return this.getSkuInfoFromDB(skuId);
    }*/

    @Override
    @GmallCache(prefix = "SkuInfo:")
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo != null) {
            LambdaQueryWrapper<SkuImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SkuImage::getSkuId,skuId);
            wrapper.eq(BaseEntity::getIsDeleted,"0");
            List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

    //根据skuId 获取最新的商品价格
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseEntity::getId,skuId);
        wrapper.eq(BaseEntity::getIsDeleted,"0");
        SkuInfo skuInfo = skuInfoMapper.selectOne(wrapper);
        if (skuInfo.getPrice()!=null){
            return new BigDecimal(String.valueOf(skuInfo.getPrice()));
        }
        return new BigDecimal("0");
    }

    //根据skuId 获取平台属性数据
    @Override
    @GmallCache(prefix = "attrList:")
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return baseAttrInfoMapper.getAttrList(skuId);
    }

    //根据spuId 获取到销售属性值Id 与skuId 组成的数据集
    @Override
    @GmallCache(prefix = "SkuValueIdsMap:")
    public Map getSkuValueIdsMap(Long spuId) {
        Map<Object, Object> map = new HashMap<>();
        // key = 125|123 ,value = 37
        List<Map> mapList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if (mapList != null && mapList.size() > 0) {
            // 循环遍历
            for (Map skuMap : mapList) {
                // key = 125|123 ,value = 37
                map.put(skuMap.get("value_ids"), skuMap.get("sku_id"));
            }
        }
        return map;
    }


}
