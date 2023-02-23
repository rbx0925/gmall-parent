package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 21:34
 * @Description
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpointUrl}")
    private String endpointUrl;

    //文件上传
    @Override
    public String fileUpload(MultipartFile file) {
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build());
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "");
            //文件上传
            String fileContentType = file.getContentType();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(fileContentType)
                            .build());
            return endpointUrl+"/"+bucketName+"/"+fileName;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件上传失败"+e);
            throw new RuntimeException("文件上传失败");
        }
    }
}
