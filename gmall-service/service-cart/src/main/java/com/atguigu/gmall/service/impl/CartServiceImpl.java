package com.atguigu.gmall.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.gmall.cart.model.CartInfo;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.product.model.SkuInfo;
import com.atguigu.gmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author rbx
 * @title
 * @Create 2023-03-08 13:34
 * @Description
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient  productFeignClient;
    //购物管理 -> 添加购物车
    @Override
    public void addToCart(String userId, Long skuId, Long skuNum) {
        String redisKey = getCartKey(userId);
        BoundHashOperations<String,String, CartInfo> hashOps = redisTemplate.boundHashOps(redisKey);
        String hasKey = skuId.toString();
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo != null) {
            CartInfo cartInfo = null;
            if (hashOps.hasKey(skuId.toString())){
                cartInfo = hashOps.get(skuId.toString());
                cartInfo.setSkuNum((int) (cartInfo.getSkuNum()+skuNum));
            }else {
                cartInfo = new CartInfo();
                cartInfo.setSkuId(skuId);
                cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
                cartInfo.setSkuName(skuInfo.getSkuName());
                cartInfo.setSkuNum(Integer.valueOf(skuNum.toString()));
                cartInfo.setIsChecked(1);
                cartInfo.setCreateTime(new Date());
                cartInfo.setUpdateTime(new Date());
                cartInfo.setUserId(userId);
                cartInfo.setCartPrice(productFeignClient.getSkuPrice(skuId));
                cartInfo.setSkuPrice(productFeignClient.getSkuPrice(skuId));
            }
            hashOps.put(hasKey,cartInfo);
        }
    }

    //购物管理 ->展示购物车列表
    @Override
    public List<CartInfo> CartList(String userId, String userTempId) {
        //用户未登录
        List<CartInfo> noLoginCartList = null;
        if (StringUtils.isNotBlank(userTempId)) {
            String noLoginCartKey = getCartKey(userTempId);
            BoundHashOperations<String,String,CartInfo> noLoginHashOps = redisTemplate.boundHashOps(noLoginCartKey);
            noLoginCartList = noLoginHashOps.values();
        }
        //临时用户登录 , 正式用户未登录 ,并且临时用户添加了商品进购物车 , 直接返回
        if (StringUtils.isBlank(userId)){
            if (!CollectionUtils.isEmpty(noLoginCartList)) {
                noLoginCartList.sort((o1, o2) -> DateUtil.truncatedCompareTo(o2.getUpdateTime(),o1.getUpdateTime(), Calendar.SECOND));
            }
            return noLoginCartList;
        }
        //用户已登录
        String loginCartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> loginHashOps = redisTemplate.boundHashOps(loginCartKey);
        if (!CollectionUtils.isEmpty(noLoginCartList)){
            //临时用户添加商品到购物车进行合并
            for (CartInfo cartInfo : noLoginCartList) {
                if (loginHashOps.hasKey(cartInfo.getSkuId().toString())){
                    //临时用户添加的商品和登录后添加的相同 , 商品数量增加
                    CartInfo loginCartInfo = loginHashOps.get(cartInfo.getSkuId().toString());
                    loginCartInfo.setSkuNum(loginCartInfo.getSkuNum()+cartInfo.getSkuNum());
                    loginHashOps.put(cartInfo.getSkuId().toString(),loginCartInfo);
                }else {
                    //临时用户添加的商品和登录后添加的不同 , 把临时用户添加的商品增加到 , 登陆后的购物车里
                    cartInfo.setUserId(userId);
                    cartInfo.setUpdateTime(new Date());
                    loginHashOps.put(cartInfo.getSkuId().toString(),cartInfo);
                }
            }
            //过河拆桥 , 购物车合并后 , 删除临时用户购物车数据
            redisTemplate.delete(getCartKey(userTempId));
        }
        List<CartInfo> allCartInfoList = loginHashOps.values();
        allCartInfoList.sort((o1, o2) -> DateUtil.truncatedCompareTo(o2.getUpdateTime(),o1.getUpdateTime(),Calendar.SECOND));
        return allCartInfoList;
    }

    //购物车管理 -> 更新选中状态
    @Override
    public void checkCart(String userId, Long skuId, int isChecked) {
        String redisKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOps = redisTemplate.boundHashOps(redisKey);
        CartInfo cartInfo = hashOps.get(skuId.toString());
        cartInfo.setIsChecked(isChecked);
        hashOps.put(skuId.toString(),cartInfo);
    }

    //购物车管理 ->删除购物车
    @Override
    public void deleteCart(Long skuId, String userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        hashOps.delete(skuId.toString());
    }

    /**
     * 获取用户对应hash操作的Key
     *
     * @param userId 用户ID
     * @return
     */
    private String getCartKey(String userId) {
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }
}
