package com.atguigu.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author rbx
 * @title
 * @Create ${YEAR}-${MONTH}-${DAY} ${TIME}
 * @Description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProductApp {
    public static void main(String[] args) {
        SpringApplication.run(ProductApp.class,args);
    }
}