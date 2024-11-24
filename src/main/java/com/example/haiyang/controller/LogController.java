package com.example.haiyang.controller;


import com.example.haiyang.dto.LogPageDTO;
import com.example.haiyang.entity.Log;
import com.example.haiyang.service.ILogService;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Cbc
 * @DateTime 2024/10/31 15:59
 * @Description
 */
@RestController
@RequestMapping("/log")
@Slf4j
public class LogController {
    @Autowired
    private ILogService service;

    @PostMapping("/save")
    public R saveLog(@RequestBody Log myLog){
        log.info("存储信息");
        return service.saveLog(myLog);
    }

    @PostMapping("/page")
    public R getPage(@RequestBody LogPageDTO logPageDTO){
        log.info("根据时间范围进行Log分页查找");
        return service.getPage(logPageDTO);
    }

    @DeleteMapping("/del/{id}")
    public R del(@PathVariable Integer id){
        log.info("删除id为{}的log", id);
       return service.del(id);
    }

    @PostMapping("/update")
    public R updateLog(@RequestBody Log l){
        log.info("更新id为{}的log", l.getId());
        return service.updateLog(l);
    }
}
