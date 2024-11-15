package com.example.haiyang.controller;

import com.example.haiyang.entity.Advice;
import com.example.haiyang.service.IAdviceService;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2024-10-31
 */
@RestController
@RequestMapping("/advice")
@Slf4j
public class AdviceController {
    @Autowired
    private IAdviceService service;

    @PostMapping("/save")
    public R saveAdvice(@RequestBody Advice advice){
        log.info("保存反馈建议");
        return  service.saveAdvice(advice);
    }

}
