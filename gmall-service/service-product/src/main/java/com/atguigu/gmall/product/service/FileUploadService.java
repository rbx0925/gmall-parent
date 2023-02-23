package com.atguigu.gmall.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author rbx
 * @title
 * @Create 2023-02-22 21:33
 * @Description
 */
public interface FileUploadService {
    //文件上传
    String fileUpload(MultipartFile file);
}
