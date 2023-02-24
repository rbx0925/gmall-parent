package com.atguigu.gmall.item.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author rbx
 * @title
 * @Create 2023-02-24 20:28
 * @Description
 */
@Component
public class ItemDegradeFeignClient implements ItemFeignClient {


    @Override
    public Result getItem(Long skuId) {
        return Result.fail();
    }
}
