package com.atguigu.gmall.item.service.impl;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
* ItemServiceImpl Tester. 
* 
* @author <Authors name> 
* @since <pre>03/01/2023</pre> 
* @version 1.0 
*/ 
public class CompletableFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "不需要返回值");
        });
        runAsync.get();

        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            //int a = 1/0;
            return "我有返回值";
        }).whenCompleteAsync((s, throwable) -> {
            System.out.println("当前线程名为:"+Thread.currentThread().getName());
            System.out.println("返回值是="+s);
            System.out.println("错误信息为"+throwable);
        }).exceptionally(throwable -> {
            System.out.println("当前线程名--"+Thread.currentThread().getName());
            System.out.println("错误信息--"+throwable.getMessage());
            return "666";
        });
        String s = supplyAsync.get();
        System.out.println(s);


    }
} 
