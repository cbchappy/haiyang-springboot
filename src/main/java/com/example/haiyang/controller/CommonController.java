package com.example.haiyang.controller;


import com.example.haiyang.service.ICommonService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Cbc
 * @DateTime 2024/10/17 15:40
 * @Description
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    ICommonService service;

    @Desc("上传文件")
    @PostMapping("/file")
    public R uploadFile(@RequestParam MultipartFile file){
        log.info("userId:{}-上传文件", MyThreadLocal.getUserId());
        return service.uploadFile(file);
    }
}
