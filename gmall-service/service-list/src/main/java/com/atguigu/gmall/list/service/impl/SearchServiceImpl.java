package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.model.*;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.product.model.BaseAttrInfo;
import com.atguigu.gmall.product.model.BaseCategoryView;
import com.atguigu.gmall.product.model.BaseTrademark;
import com.atguigu.gmall.product.model.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author rbx
 * @title
 * @Create 2023-03-03 17:25
 * @Description
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RedisTemplate redisTemplate;
    //索引库名称
    private static final String index_name = "goods";

    //测试接口，商品文档对象录入索引
    @Override
    public void upperGoods(Long skuId) {
        try {
            Goods goods = new Goods();
            goods.setId(skuId);
            //根据skuId查到商品信息
            CompletableFuture<SkuInfo> skuInfoCompletableFuture  = CompletableFuture.supplyAsync(() -> {
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                if (skuInfo != null) {
                    goods.setTitle(skuInfo.getSkuName());
                    goods.setDefaultImg(skuInfo.getSkuDefaultImg());
                    goods.setCreateTime(skuInfo.getCreateTime());
                    goods.setCreatedDate(skuInfo.getCreateTime());
                }
                return skuInfo;
            });

            //查询商品价格
            CompletableFuture<Void> priceVoidCompletableFuture  = CompletableFuture.runAsync(() -> {
                BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
                goods.setPrice(skuPrice.doubleValue());
            });


            //查询分类信息
            CompletableFuture<Void> baseCategoryViewCompletableFuture  = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
                BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                if (categoryView != null) {
                    goods.setCategory1Id(categoryView.getCategory1Id());
                    goods.setCategory1Name(categoryView.getCategory1Name());
                    goods.setCategory2Id(categoryView.getCategory2Id());
                    goods.setCategory2Name(categoryView.getCategory2Name());
                    goods.setCategory3Id(categoryView.getCategory3Id());
                    goods.setCategory3Name(categoryView.getCategory3Name());
                }
            });

            //查询品牌信息
            CompletableFuture<Void> trademarkCompletableFuture  = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
                BaseTrademark trademark = productFeignClient.getTrademarkById(skuInfo.getTmId());
                if (trademark != null) {
                    goods.setTmId(trademark.getId());
                    goods.setTmName(trademark.getTmName());
                    goods.setTmLogoUrl(trademark.getLogoUrl());
                }
            });


            //查询平台属性
            CompletableFuture<Void> atrrCompletableFuture  = CompletableFuture.runAsync(() -> {
                List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
                if (!CollectionUtils.isEmpty(attrList)) {
                    attrList.stream().map(baseAttrInfo -> {
                        SearchAttr searchAttr = new SearchAttr();
                        searchAttr.setAttrId(baseAttrInfo.getId());
                        searchAttr.setAttrName(baseAttrInfo.getAttrName());
                        searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
                        return searchAttr;
                    }).collect(Collectors.toList());
                }
            });

            CompletableFuture.allOf(skuInfoCompletableFuture,
                    priceVoidCompletableFuture,
                    baseCategoryViewCompletableFuture,
                    atrrCompletableFuture,
                    trademarkCompletableFuture).join();

            IndexRequest request = new IndexRequest(index_name);
            request.id(skuId.toString());
            request.source(JSON.toJSONString(goods), XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[搜索服务]-上架商品异常:{}", e);
            throw new RuntimeException("上架商品异常:" + e.getMessage());
        }
    }

    //测试接口，商品文档删除
    @Override
    public void lowerGoods(Long skuId) {
        try {
            DeleteRequest request = new DeleteRequest(index_name,skuId.toString());
            restHighLevelClient.delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[搜索服务]-删除商品异常:{}", e);
            throw new RuntimeException("删除商品异常:" + e.getMessage());
        }
    }

    //更新商品的热度排名分值
    @Override
    public void incrHotScore(Long skuId) {
        String hotKey = "hotScore";
        try {
            Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey, skuId.toString(), 1);
            if (hotScore%10==0){
                GetRequest getRequest = new GetRequest(index_name, skuId.toString());
                GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
                String source = response.getSourceAsString();
                Goods goods = JSON.parseObject(source, Goods.class);
                goods.setHotScore(hotScore.longValue());

                UpdateRequest updateRequest = new UpdateRequest(index_name, skuId.toString());
                updateRequest.doc(JSON.toJSONString(goods),XContentType.JSON);
                restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[更新文档热度分值失败:{}]", e);
        }
    }

    //从索引库goods中查询业务数据同时，聚合过滤条件
    //商品检索 使用SDE通过调用ES提供JavaClientAPI构建DSL语句-请求地址,请求体参数;解析ES响应结果
    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        try {
            //一. 构建SearchRequest封装 , 检索索引库名称 , 请求体包含:查询方式 分页, 高亮, 排序, 聚合
            SearchRequest searchRequest = this.buiderDsl(searchParam);
            System.out.println(searchRequest.source().toString());
            System.out.println("--------------------------------");
            //2. 执行检索 , 得到响应结果
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //3. 按照要求封装响应结果
            return this.parseResult(response,searchParam);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[检索商品]异常:{}");
        }
        return new SearchResponseVo();
    }

    //目的:构建调用ES Http接口 请求信息(请求地址,方式,路径参数,请求体参数)
    private SearchRequest buiderDsl(SearchParam searchParam) {
        SearchRequest searchRequest = new SearchRequest(index_name);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder allBoolQueryBuilder = QueryBuilders.boolQuery();
        //设置关键字条件设置
        if (StringUtils.isNotBlank(searchParam.getKeyword())){
            allBoolQueryBuilder.must(QueryBuilders.matchQuery("title",searchParam.getKeyword()).operator(Operator.AND));
        }
        if (StringUtils.isNotBlank(searchParam.getTrademark())){
            String[] split = searchParam.getTrademark().split(":");
            if (split!=null&&split.length==2){
                allBoolQueryBuilder.filter(QueryBuilders.termQuery("tmId",split[0]));
            }
        }
        //设置分类条件过滤
        if (searchParam.getCategory1Id()!=null){
            allBoolQueryBuilder.filter(QueryBuilders.termQuery("category1Id", searchParam.getCategory1Id()));
        }
        if (searchParam.getCategory2Id()!=null){
            allBoolQueryBuilder.filter(QueryBuilders.termQuery("category2Id", searchParam.getCategory2Id()));
        }
        if (searchParam.getCategory3Id()!=null){
            allBoolQueryBuilder.filter(QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id()));
        }
        //设置平台属性 , 条件过滤 , 形式 , 平台属性Id, 平台属性名称 , 平台属性名
        String[] props = searchParam.getProps();
        if (props!=null&&props.length>0){
            BoolQueryBuilder attrBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String prop : props) {
                String[] split = prop.split(":");
                if (split!=null&&split.length==3){
                    BoolQueryBuilder attrIdAndAttrValueBoolQueryBuilder = QueryBuilders.boolQuery();
                    attrIdAndAttrValueBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                    attrIdAndAttrValueBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));
                    NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", attrBoolQueryBuilder, ScoreMode.None);
                    attrBoolQueryBuilder.must(nestedQueryBuilder);
                }
            }
            allBoolQueryBuilder.filter(attrBoolQueryBuilder);
        }
        sourceBuilder.query(allBoolQueryBuilder);
        //设置分页
        int from = (searchParam.getPageNo()-1)*searchParam.getPageSize();
        sourceBuilder.from(from).size(searchParam.getPageSize());
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("title");
        sourceBuilder.highlighter(highlightBuilder);
        //设置排序 sort()
        if (StringUtils.isNotBlank(searchParam.getOrder())){
            String[] split = searchParam.getOrder().split(":");
            if (split!=null&&split.length==2){
                String orderField = "";
                switch (split[0]){
                    case "1":
                        orderField = "hotScore";
                        break;
                    case "2":
                        orderField = "price";
                        break;
                }
                sourceBuilder.sort(orderField,"asc".equals(split[1])? SortOrder.ASC:SortOrder.DESC);
            }
        }
        //设置过滤字段
        sourceBuilder.fetchSource(new String[]{"id","title","price","defaultImg"},null);
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId");
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"));
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmLgoUrlAgg").field("tmLogoUrl"));
        sourceBuilder.aggregation(tmIdAgg);
        //设置平台属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrsAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);
        return searchRequest.source(sourceBuilder);
    }


    /**
     * 解析ES响应结果:业务数据结果;聚合结果
     *
     * @param response
     * @param searchParam
     * @return
     */
    private SearchResponseVo parseResult(SearchResponse response, SearchParam searchParam) {
        //1.创建搜索响应VO对象
        SearchResponseVo vo = new SearchResponseVo();

        //2.封装分页信息
        vo.setPageNo(searchParam.getPageNo());
        Integer pageSize = searchParam.getPageSize();
        vo.setPageSize(pageSize);
        //2.1 获取总记录数
        long total = response.getHits().getTotalHits().value;
        vo.setTotal(total);
        //2.2 计算总页数 总数%页大小能整除=总数/页大小  反之+1
        Long totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        vo.setTotalPages(totalPage);

        //3.封装检索到商品数据-注意处理高亮字段
        SearchHit[] hits = response.getHits().getHits();
        List<Goods> goodsList = new ArrayList<>();
        if (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                //3.1 将得到商品JSON字符串转为Java对象
                Goods goods = JSON.parseObject(hit.getSourceAsString(), Goods.class);
                //3.2 处理高亮
                if(!CollectionUtils.isEmpty(hit.getHighlightFields())){
                    Text[] titles = hit.getHighlightFields().get("title").getFragments();
                    if (titles != null && titles.length > 0) {
                        goods.setTitle(titles[0].toString());
                    }
                }
                goodsList.add(goods);
            }
        }
        vo.setGoodsList(goodsList);

        //4.封装品牌聚合结果
        Map<String, Aggregation> allAggregationMap = response.getAggregations().asMap();
        //4.1 获取品牌ID聚合对象 通过获取品牌ID桶得到聚合品牌ID
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) allAggregationMap.get("tmIdAgg");
        if (tmIdAgg != null) {
            List<SearchResponseTmVo> tmVoList = tmIdAgg.getBuckets().stream().map(bucket -> {
                SearchResponseTmVo tmVo = new SearchResponseTmVo();
                long tmId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
                tmVo.setTmId(tmId);
                //4.2 从品牌Id桶内获取品牌名称聚合对象,遍历品牌名称桶得到桶中品牌名称-只有一个
                ParsedStringTerms tmNameAgg = ((Terms.Bucket) bucket).getAggregations().get("tmNameAgg");
                if (tmNameAgg != null) {
                    String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
                    tmVo.setTmName(tmName);
                }
                //4.3 从品牌Id桶内获取品牌图片聚合对象,遍历品牌图片桶得到桶中图片Logo-只有一个
                ParsedStringTerms tmLgoUrlAgg = ((Terms.Bucket) bucket).getAggregations().get("tmLgoUrlAgg");
                if (tmLgoUrlAgg != null) {
                    String tmLogoUrl = tmLgoUrlAgg.getBuckets().get(0).getKeyAsString();
                    tmVo.setTmLogoUrl(tmLogoUrl);
                }
                return tmVo;
            }).collect(Collectors.toList());

            //为响应对象封装聚合品牌列表
            vo.setTrademarkList(tmVoList);
        }

        //5.封装平台属性聚合结果

        //5.1 获取平台属性聚合对象
        ParsedNested attrsAgg = (ParsedNested) allAggregationMap.get("attrsAgg");
        //5.2 通过平台数据聚合对象获取平台属性ID的聚合对象,获取平台属性ID聚合桶集合
        if (attrsAgg != null) {
            ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
            if (attrIdAgg != null) {
                //5.3 遍历ID桶集合 获取平台属性ID 以及平台属性名称跟属性值
                List<SearchResponseAttrVo> attrVoList = attrIdAgg.getBuckets().stream().map(bucket -> {
                    SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
                    //获取平台属性Id
                    long atrrId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
                    attrVo.setAttrId(atrrId);
                    //5.3.1 基于平台属性ID聚合对象 获取平台属性名称子聚合对象.获取平台名称桶内平台属性名称 只有一个
                    ParsedStringTerms attrNameAgg = ((Terms.Bucket) bucket).getAggregations().get("attrNameAgg");
                    if (attrNameAgg != null) {
                        String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                        attrVo.setAttrName(attrName);
                    }
                    //5.3.2 基于平台属性ID聚合对象 获取平台属性值子聚合对象.获取平台属性值桶内平台属性值名称
                    ParsedStringTerms attrValueAgg = ((Terms.Bucket) bucket).getAggregations().get("attrValueAgg");
                    if (attrValueAgg != null) {
                        //遍历平台属性值桶,得到桶内每个平台属性值
                        List<String> attrValueList = attrValueAgg.getBuckets().stream().map(attrValueBucket -> {
                            return ((Terms.Bucket) attrValueBucket).getKeyAsString();
                        }).collect(Collectors.toList());

                        attrVo.setAttrValueList(attrValueList);
                    }
                    return attrVo;
                }).collect(Collectors.toList());
                //给响应VO对象赋值:平台属性集合
                vo.setAttrsList(attrVoList);
            }
        }
        return vo;
    }
}
