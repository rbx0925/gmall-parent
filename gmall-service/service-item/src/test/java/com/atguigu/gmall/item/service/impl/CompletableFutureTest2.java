package com.atguigu.gmall.item.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author rbx
 * @title
 * @Create 2023-03-02 12:57
 * @Description
 */
public class CompletableFutureTest2 {
    /**
     * A 线程有计算结果
     * B 线程依赖A线程计算结果，执行B线程任务
     * C 线程依赖A线程计算结果，执行C线程任务
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.创建异步任务对象 CompletableFuture  A任务需要返回值
        CompletableFuture<Long> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("A任务执行");
            return 29L;
        });

        //2.基于上面对象 构建 B任务对象
        CompletableFuture<String> futureB = futureA.thenApplyAsync((aResult) -> {
            try {
                Thread.sleep(5000);
                System.out.println("B任务执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "skuID为" + aResult + "的销售属性";
        });

        //3.基于上面对象 构建 C任务对象
        CompletableFuture<String> futureC = futureA.thenApplyAsync((aResult) -> {
            try {
                Thread.sleep(11);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("C任务执行");
            return "skuID为" + aResult + "的海报列表";
        });

        /*futureA.join();
        futureB.join();
        futureC.join();

        Long a = futureA.get();
        System.out.println(a);
        String b = futureB.get();
        System.out.println(b);
        String c = futureC.get();
        System.out.println(c);*/


        //CompletableFuture.anyOf(futureA, futureB, futureC).join();
        CompletableFuture.allOf(futureA, futureB, futureC).join();
        System.out.println("执行后续业务代码");
    }
}
