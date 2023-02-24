package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.model.BaseCategoryView;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rbx
 * @title
 * @Create 2023-02-23 21:33
 * @Description
 */
@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    //根据三级分类id获取分类信息
    @Override
    @GmallCache(prefix = "categoryView:")
    public BaseCategoryView getCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Override
    @GmallCache(prefix = "baseCategoryList:")
    public List<JSONObject> getBaseCategoryList() {
        //0.构建响应结果对象
        List<JSONObject> resultList = new ArrayList<>();
        //1.查询所有分类视图表记录
        List<BaseCategoryView> allCategoryviewList = baseCategoryViewMapper.selectList(null);

        //2.对所有分类根据category1Id属性进行分组，得到所有一级分类（包含二级跟三级）
        if (!CollectionUtils.isEmpty(allCategoryviewList)) {
            //2.1 采用Stream流进行分组 分组后Map中key:getCategory1Id一级分类ID  val:所有一级分类数据
            Map<Long, List<BaseCategoryView>> category1Map = allCategoryviewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
            //2.2 遍历一级分类
            int index = 1;
            for (Map.Entry<Long, List<BaseCategoryView>> entry : category1Map.entrySet()) {
                //2.2.1 获取一级分类ID
                Long category1Id = entry.getKey();
                //2.2.2 获取一级分类名称
                String category1Name = entry.getValue().get(0).getCategory1Name();
                //2.2.3 创建一级分类JSON对象
                JSONObject category1 = new JSONObject();
                category1.put("index", index++);
                category1.put("categoryId", category1Id);
                category1.put("categoryName", category1Name);

                //3.从每个一级分类Map中获取二级分类，处理二级分类数据
                //3.1 声明二级分类List集合
                List<JSONObject> category2List = new ArrayList<>();
                //3.2 从一级分类map中获去所有一级分类数据，根据二级分类ID分组
                Map<Long, List<BaseCategoryView>> category2MapList = entry.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
                //3.3 遍历二级分类 构建二级分类JSON对象
                for (Map.Entry<Long, List<BaseCategoryView>> category2Entry : category2MapList.entrySet()) {
                    JSONObject category2 = new JSONObject();
                    category2.put("categoryId", category2Entry.getKey());
                    category2.put("categoryName", category2Entry.getValue().get(0).getCategory2Name());
                    category2List.add(category2);

                    //4. 从每个二级分类中获取三级分类 只需要从category2Entry Map获取val
                    List<BaseCategoryView> category3ArrayList = category2Entry.getValue();
                    //4.1 声明三级分类集合
                    List<JSONObject> category3List = new ArrayList<>();
                    //4.2 遍历集合 构建三级分类对象 将三级分类加入到二级分类对象中
                    for (BaseCategoryView baseCategoryView : category3ArrayList) {
                        JSONObject category3 = new JSONObject();
                        category3.put("categoryId", baseCategoryView.getCategory3Id());
                        category3.put("categoryName", baseCategoryView.getCategory3Name());
                        category3List.add(category3);
                    }
                    //4.3 将三级分类集合加入到二级分类中
                    category2.put("categoryChild", category3List);
                }
                //将二级分类集合加入一级分类对象中
                category1.put("categoryChild", category2List);
                //将处理后一级分类加入结果
                resultList.add(category1);
            }
        }
        return resultList;
    }
}
