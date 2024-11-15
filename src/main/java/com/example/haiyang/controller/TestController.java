package com.example.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.annotation.RecordOperateLog;
import com.example.haiyang.entity.Test;
import com.example.haiyang.service.ITestService;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Cbc
 * @DateTime 2024/10/11 12:07
 * @Description
 */

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    ITestService service;

    @GetMapping("/test/{msg}")
    @RecordOperateLog
    public R test(@PathVariable String msg) throws InterruptedException {
      log.info("访问了test");
        Thread.sleep(1000);
        return R.success("成功了", null);
    }
}
