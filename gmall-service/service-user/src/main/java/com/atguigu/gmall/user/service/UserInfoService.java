package com.atguigu.gmall.user.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.model.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户表 业务接口类
 * @author atguigu
 * @since 2023-03-07
 */
public interface UserInfoService extends IService<UserInfo> {

    //用户登录管理
    Result login(UserInfo userInfo, HttpServletRequest request);

    //用户退出系统
    void logout(String token);
}
