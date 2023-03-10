package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.model.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author rbx
 * @title
 * @Create 2023-03-07 18:09
 * @Description
 */
@RestController
@RequestMapping("/api/user")
public class PassportController {
    @Autowired
    private UserInfoService userInfoService;

    //用户登录管理
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request){
        return userInfoService.login(userInfo,request);
    }

    //用户退出系统
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token")String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}
