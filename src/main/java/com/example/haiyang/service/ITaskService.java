package com.example.haiyang.service;

import com.example.haiyang.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.util.R;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
public interface ITaskService extends IService<Task> {

    R addTask(Task task);

    R deleteTask(Integer id);

    R updateTask(Task task);

    R getTaskByUserId(Integer userId);
}
