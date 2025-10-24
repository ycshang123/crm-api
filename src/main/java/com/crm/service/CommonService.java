package com.crm.service;

import com.crm.vo.FileUrlVO;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    /**
     * 文件上传
     *
     * @param uploadFile
     * @return
     */
    FileUrlVO upload(MultipartFile uploadFile);
}
