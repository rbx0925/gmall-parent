package com.atguigu.gmall.item.service.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author rbx
 * @title
 * @Create 2023-03-02 12:44
 * @Description
 */
public class CompletableFutureTest1 {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 500, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));

        // 线程1执行返回的结果：hello
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> "hello");

        // 线程2 获取到线程1执行的结果
        CompletableFuture<Void> futureB = futureA.thenAcceptAsync((s) -> {
            delaySec(1);
            printCurrTime(s + " 第一个线程");
        }, threadPoolExecutor);

        CompletableFuture<Void> futureC = futureA.thenAcceptAsync((s) -> {
            delaySec(2);
            printCurrTime(s + " 第二个线程");
        }, threadPoolExecutor);

        threadPoolExecutor.shutdown();
    }


    private static void printCurrTime(String str) {
        System.out.println(str);
    }

    private static void delaySec(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
