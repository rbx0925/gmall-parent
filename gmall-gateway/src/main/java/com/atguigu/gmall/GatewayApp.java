package com.atguigu.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rbx
 * @title
 * @Create ${YEAR}-${MONTH}-${DAY} ${TIME}
 * @Description
 */
@SpringBootApplication
public class GatewayApp {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApp.class, args);
        System.out.println("Hello world!");
    }
}