package com.example.haiyang.controller;


import com.example.haiyang.dto.LogPageDTO;
import com.example.haiyang.entity.Log;
import com.example.haiyang.service.ILogService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.MyThreadLocal;
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

    @Desc("存储记事簿信息")
    @PostMapping("/save")
    public R saveLog(@RequestBody Log myLog){
        log.info("userId:{}-存储记事簿信息", MyThreadLocal.getUserId());
        return service.saveLog(myLog);
    }

    @Desc("根据时间范围进行Log分页查找")
    @PostMapping("/page")
    public R getPage(@RequestBody LogPageDTO logPageDTO){
        log.info("userId:{}-根据时间范围进行Log分页查找", MyThreadLocal.getUserId());
        return service.getPage(logPageDTO);
    }

    @Desc("删除记事本的一条记录")
    @DeleteMapping("/del/{id}")
    public R del(@PathVariable Integer id){
        log.info("userId:{}-删除id为{}的log", MyThreadLocal.getUserId(), id);
       return service.del(id);
    }

    @Desc("更新记事本")
    @PostMapping("/update")
    public R updateLog(@RequestBody Log l){
        log.info("userId:{}-更新id为{}的log", MyThreadLocal.getUserId(), l.getId());
        return service.updateLog(l);
    }
}
