package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rbx
 * @title
 * @Create 2023-02-27 18:18
 * @Description
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("admin/product/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/testLock")
    public Result testLock(){
        //testService.testLock();
        //采用SpringDataRedis实现分布式锁
        //testService.testLock2();
        //使用Redison实现分布式锁
        testService.testLock3();
        return Result.ok();
    }
}
