package com.atguigu.gmall.service;

import com.atguigu.gmall.cart.model.CartInfo;

import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-03-08 13:34
 * @Description
 */
public interface CartService {
    //购物管理 -> 添加购物车
    void addToCart(String userId, Long skuId, Long skuNum);

    //购物管理 ->展示购物车列表
    List<CartInfo> CartList(String userId, String userTempId);

    //购物车管理 -> 更新选中状态
    void checkCart(String userId, Long skuId, int isChecked);

    //购物车管理 ->删除购物车
    void deleteCart(Long skuId, String userId);
}
