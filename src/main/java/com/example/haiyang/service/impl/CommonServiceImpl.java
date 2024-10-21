package com.example.haiyang.service.impl;

import cn.hutool.core.lang.UUID;
import com.example.haiyang.service.ICommonService;
import com.example.haiyang.util.MyUtil;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Cbc
 * @DateTime 2024/10/17 21:51
 * @Description
 */
@Service
@Slf4j
public class CommonServiceImpl implements ICommonService {
    @Override
    public R uploadFile(MultipartFile file) {
        log.info("name:{}", file.getName());
        log.info("originalName:{}", file.getOriginalFilename());
        int index = file.getOriginalFilename().lastIndexOf(".");
        String suffix = file.getOriginalFilename().substring(index );
        String key = "avatar/" + UUID.randomUUID() + suffix;
        String url = MyUtil.uploadFile(file, key);
        log.info("返回的url{}", url);
        return  R.success(url);
    }
}
