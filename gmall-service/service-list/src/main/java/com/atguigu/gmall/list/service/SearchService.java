package com.atguigu.gmall.list.service;

import com.atguigu.gmall.list.model.SearchParam;
import com.atguigu.gmall.list.model.SearchResponseVo;

/**
 * @author rbx
 * @title
 * @Create 2023-03-03 17:25
 * @Description
 */
public interface SearchService {
    //测试接口，商品文档对象录入索引
    void upperGoods(Long skuId);

    //测试接口，商品文档删除
    void lowerGoods(Long skuId);

    //更新商品的热度排名分值
    void incrHotScore(Long skuId);

    //商品检索
    SearchResponseVo search(SearchParam searchParam);
}
