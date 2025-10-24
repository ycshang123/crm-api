package com.crm.controller;


import com.crm.common.result.Result;
import com.crm.service.CommonService;
import com.crm.vo.FileUrlVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-21 16:34
 **/
@Api(tags = "通用模块")
@RestController
@RequestMapping("common")
@AllArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @PostMapping(value = "/upload/file")
    @ResponseBody
    @ApiOperation("文件上传")
    public Result<FileUrlVO> upload(@RequestBody MultipartFile file) {
        return Result.ok(commonService.upload(file));
    }

}
