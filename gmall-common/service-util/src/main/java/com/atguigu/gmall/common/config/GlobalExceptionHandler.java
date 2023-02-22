package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author rbx
 * @title
 * @Create 2023-02-21 20:36
 * @Description全局异常处理
 * 作用：拦截到controller层异常，将异常结果统一进行友好返回 返回表中响应结果Result
 */
@Slf4j
@RestControllerAdvice //  @ControllerAdvice+@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public Result error(RuntimeException e) {
        log.error("全局运行时异常，{}", e);
        return Result.fail();
    }


    /**
     * 处理异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        log.error("全局异常，{}", e);
        return Result.fail();
    }
}
