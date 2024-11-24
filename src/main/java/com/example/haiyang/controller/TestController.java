package com.example.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.entity.Test;
import com.example.haiyang.service.ITestService;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/test/{msg}")
    public R test(@PathVariable String msg) throws InterruptedException {
      log.info("访问了test");
        Thread.sleep(1000);
        return R.success("成功了", null);
    }

    @GetMapping("/kafka")
    public R testKafka(){

        return R.success("成功");
    }

    @PostMapping("/exception")
    public R testExceptionLog(){
        log.info("testExceptionLog");
        try {
            g();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return R.success("nnn", "111");
    }


    public static void f() throws Exception {

        Exception exception = new Exception("Thrown from f()");
        Throwable c1 = new Throwable("这是cause1");
        c1.initCause(new Throwable("这是cause2"));
        exception.initCause(c1);
        throw exception;
    }

    public static void g() throws Exception {
        f();
    }


}
