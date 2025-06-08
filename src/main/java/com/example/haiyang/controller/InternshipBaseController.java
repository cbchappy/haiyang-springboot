package com.example.haiyang.controller;

import com.example.haiyang.service.IInternshipBaseService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@RestController
@RequestMapping("/internshipBase")
public class InternshipBaseController {
    @Autowired
    private IInternshipBaseService service;

    @Desc("获取所有实习基地")
    @GetMapping("/all")
    public R getAll(){
        return service.getAll();
    }
}
