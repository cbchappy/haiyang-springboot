package com.example.haiyang.service;

import com.example.haiyang.util.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Cbc
 * @DateTime 2024/10/17 21:50
 * @Description
 */
public interface ICommonService {
    R uploadFile(MultipartFile file);
}
