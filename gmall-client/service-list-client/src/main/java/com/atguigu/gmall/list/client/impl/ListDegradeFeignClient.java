package com.atguigu.gmall.list.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.list.model.SearchParam;
import com.atguigu.gmall.list.model.SearchResponseVo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author rbx
 * @title
 * @Create 2023-03-03 19:37
 * @Description
 */
@Component
public class ListDegradeFeignClient implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId) {
        return null;
    }

    @Override
    public Result upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result lowerGoods(Long skuId) {
        return null;
    }

    @Override
    public Result<Map> search(SearchParam searchParam) {
        return null;
    }

}
