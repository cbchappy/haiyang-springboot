package com.example.haiyang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.entity.Test;
import com.example.haiyang.dao.TestMapper;
import com.example.haiyang.service.ITestService;
import org.springframework.stereotype.Service;

/**
 * @Author Cbc
 * @DateTime 2024/10/11 12:32
 * @Description
 */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {
}
