package com.example.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.entity.Test;
import com.example.haiyang.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Cbc
 * @DateTime 2024/10/11 12:07
 * @Description
 */

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    ITestService service;

    @GetMapping("/test")
    public String test(){

        Test test = new Test();
        test.setInfo("成功了");
        boolean save = service.save(test);
        System.out.println(test.getId());
        QueryWrapper<Test> wrapper = new QueryWrapper<>();

        return service.getById(test.getId()).getInfo();
    }
}
