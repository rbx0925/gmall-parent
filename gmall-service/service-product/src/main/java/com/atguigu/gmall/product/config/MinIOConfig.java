package com.atguigu.gmall.product.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 23:32
 * @Description
 */
@Configuration
public class MinIOConfig {

    @Value("${minio.endpointUrl}")
    private String endpointUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secreKey}")
    private String secreKey;

    @Bean
    public MinioClient minioClient(){
        return
                MinioClient.builder()
                        .endpoint(endpointUrl)
                        .credentials(accessKey, secreKey)
                        .build();
    }
}
