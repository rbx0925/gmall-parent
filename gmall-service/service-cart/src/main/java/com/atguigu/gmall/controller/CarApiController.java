package com.atguigu.gmall.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.gmall.cart.model.CartInfo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author rbx
 * @title
 * @Create 2023-03-08 13:32
 * @Description
 */
@RestController
@RequestMapping("/api/cart")
public class CarApiController {
    @Autowired
    private CartService cartService;

    //购物管理 -> 添加购物车
    @RequestMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum")Long skuNum, HttpServletRequest request){
        String userId = getUserId(request);
        cartService.addToCart(userId,skuId,skuNum);
        return Result.ok();
    }

    //购物管理 ->展示购物车列表
    @GetMapping("/cartList")
    public Result cartList(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartInfoList = cartService.CartList(userId,userTempId);
        return Result.ok(cartInfoList);
    }

    //购物车管理 -> 更新选中状态
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId")Long skuId,@PathVariable("isChecked")int isChecked,HttpServletRequest request){
        String userId = getUserId(request);
        cartService.checkCart(userId,skuId,isChecked);
        return Result.ok();
    }

    //购物车管理 ->删除购物车
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId")Long skuId,HttpServletRequest request){
        String userId = getUserId(request);
        cartService.deleteCart(skuId,userId);
        return Result.ok();
    }

    private static String getUserId(HttpServletRequest request) {
        String userId = "";
        userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isBlank(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        return userId;
    }
}
