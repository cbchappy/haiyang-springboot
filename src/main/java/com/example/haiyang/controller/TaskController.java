package com.example.haiyang.controller;

import com.example.haiyang.entity.Task;
import com.example.haiyang.service.ITaskService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private ITaskService service;

    @Desc("添加任务")
    @PostMapping("/add")
    public R addTask(@RequestBody Task task){
        return service.addTask(task);
    }

    @Desc("删除任务")
    @DeleteMapping("/{id}")
    public R deleteTask(@PathVariable Integer id){
        return service.deleteTask(id);
    }

    @Desc("更新任务")
    @PostMapping("/update")
    public R updateTask(@RequestBody Task task){
        return service.updateTask(task);
    }
    @Desc("获取用户任务")
    @GetMapping("/{userId}")
    public R getTaskByUserId(@PathVariable Integer userId){
        return service.getTaskByUserId(userId);
    }

}
