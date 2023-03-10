package com.atguigu.gmall.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.utils.BaiduMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * 身份认证过滤器
 *
 * @author: atguigu
 * @create: 2023-03-07 14:28
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${authUrls.url}")
    private List<String> authUrlList;


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 验证用户请求是否合法
     *
     * @param exchange 封装请求对象,响应对象
     * @param chain    过滤器链
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //通过exchange获取到请求对象以及响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取用户请求地址
        String path = request.getURI().getPath();
        //1.对于那些静态资源同步请求 img js css 直接放行-无论是否登录
        if (antPathMatcher.match("/*/img/**", path) || antPathMatcher.match("/*/js/**", path) || antPathMatcher.match("/*/css/**", path)) {
            return chain.filter(exchange);
        }

        //2.对于微服务间调用Feign接口不允许用户直接来方法 "/inner" --无论是否登录
        if (antPathMatcher.match("/**/inner/**", path)) {
            //响应错误信息
            return outError(response, ResultCodeEnum.ILLEGAL_REQUEST);
        }

        String userId = this.getUserId(request, response);
        if (StringUtils.isBlank(userId)) {
            //3.如果用户没有登录-并且还要访问(需要登录)restuful接口 例如:-返回拒绝访问
            if (antPathMatcher.match("/**/auth/**", path)) {
                //响应错误信息
                return outError(response, ResultCodeEnum.PERMISSION);
            }
            //4.如果用户没有登录-并且访问(需要登录)页面请求-引导用户去登录
            if (!CollectionUtils.isEmpty(authUrlList)) {
                for (String auth : authUrlList) {
                    if (antPathMatcher.match("/" + auth + "*", path)) {
                        //引导用户登录
                        //说明用户未登录 访问地址要求登录 设置重定向 需要将http状态码设置为301
                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        //通过Response对象 响应头设置重定向登录地址
                        response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originUrl=" + request.getURI().toString());
                        //结束
                        return response.setComplete();
                    }
                }
            }
        }

        //5.如果用户登录-将用户ID设置到请求头中-将用户ID参数值透传到业务微服务
        if (StringUtils.isNotBlank(userId)) {
            //为了能在其他微服务中获取用户ID 需要将用户ID存入
            request.mutate().header("userId", userId);
        }
        //将临时用户id添加到请求头
        String userTempId = getUserTempId(request);
        if (StringUtils.isNotBlank(userTempId)) {
            request.mutate().header("userTempId",userTempId);
        }
        return chain.filter(exchange);
    }


    /**
     * 尝试从Redis中获取用户ID
     *
     * @param request
     * @param response
     * @return
     */
    private String getUserId(ServerHttpRequest request, ServerHttpResponse response) {
        //1.先从请求头中获取Token
        String token = "";
        token = request.getHeaders().getFirst("token");
        if (StringUtils.isBlank(token)) {
            //2.在尝试从请求对象中Cookie中获取
            HttpCookie httpCookie = request.getCookies().getFirst("token");
            if (httpCookie != null) {
                token = httpCookie.getValue();
            }
        }

        //3.根据Token查询Redis中用户信息-判断用户是否为异地登录 todo 是否更换设备
        if (StringUtils.isNotBlank(token)) {
            String redisKey = "user:login:" + token;
            HashMap<String, String> userInfoMap = (HashMap<String, String>) redisTemplate.opsForValue().get(redisKey);
            if (userInfoMap != null) {
                //用户登录时所在城市
                String loginCity = userInfoMap.get("city");
                //获取本地访问所在城市
                String ipAddress = IpUtil.getGatwayIpAddress(request);
                String nowCity = BaiduMapUtil.getAddress("114.240.253.58");
                if (!nowCity.equals(loginCity)) {
                    this.outError(response, ResultCodeEnum.ILLEGAL_REQUEST);
                }
                //4.返回用户ID
                String userId = userInfoMap.get("userId");
                return userId;
            }
        }
        return null;
    }

    /**
     * 尝试获取临时用户的id
     * @param request
     * @return
     */
    private String getUserTempId(ServerHttpRequest request){
        String userTempId = "";
        List<HttpCookie> cookieList = request.getCookies().get("userTempId");
        if (!CollectionUtils.isEmpty(cookieList)){
            userTempId = cookieList.get(0).getValue();
        }
        String first = request.getHeaders().getFirst("userTempId");
        if (StringUtils.isNotBlank(first)){
            userTempId = first;
        }
        return userTempId;
    }


    /**
     * 用来给前端响应错误提示信息
     *
     * @param response
     * @param resultCodeEnum
     * @return
     */
    private Mono<Void> outError(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        //1.准备响应结果对象,转为JSON对象
        Result<Object> result = Result.build(null, resultCodeEnum);
        String resultString = JSON.toJSONString(result);

        //2.响应结果给客户端
        //2.1 设置http状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //2.2 通过响应头设置响应数据格式-json
        response.getHeaders().add("content-type", "application/json;charset=utf-8");
        DataBuffer wrap = response.bufferFactory().wrap(resultString.getBytes());
        //2.3 网关结束响应-不再路由转发
        //return response.setComplete();
        //2.4 网关将响应数据返回给客户端
        return response.writeWith(Mono.just(wrap));
    }



    /**
     * 值越小优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }


    public static void main(String[] args) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("*/user/**", "d/user/a/b/c");
        System.out.println(match);
    }
}