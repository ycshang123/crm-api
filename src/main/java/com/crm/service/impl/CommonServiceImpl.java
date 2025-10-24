package com.crm.service.impl;


import com.aliyun.oss.OSSClient;
import com.crm.service.CommonService;
import com.crm.vo.FileUrlVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-21 16:30
 **/
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private OSSClient ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Override
    public FileUrlVO upload(MultipartFile uploadFile) {

        String returnFileUrl = "";

        // 获取文件原名称
        String originalFilename = uploadFile.getOriginalFilename();
        // 获取文件类型
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 新文件名称
        String newFileName = UUID.randomUUID() + fileType;

        // 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //文件上传至阿里云OSS
        ossClient.putObject(bucketName, newFileName, inputStream);
        // 获取文件上传后的图片返回地址
        returnFileUrl = "https://" + bucketName + "." + ossClient.getEndpoint().getHost() + "/" + newFileName;

        return new FileUrlVO(returnFileUrl);

    }


}
