package com.example.haiyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.haiyang.entity.Task;
import com.example.haiyang.dao.TaskMapper;
import com.example.haiyang.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.haiyang.common.constants.RedisConstants.TASK_REMIND;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private StringRedisTemplate template;

    @Override
    public R addTask(Task task) {
        if(task.getRemindTime() == null){
            return R.failMsg("任务提醒时间不能为空");
        }
        if(task.getDescription() == null){
            return R.failMsg("任务描述不能为空");
        }
        task.setCreateTime(LocalDateTime.now());
        task.setUserId(MyThreadLocal.getUserId());
        mapper.insert(task);
        long remindMills = task.getRemindTime().getNano() / 1000;
        template.opsForZSet().add(TASK_REMIND, String.valueOf(task.getId()), remindMills);
        return R.success(task);
    }

    @Override
    public R deleteTask(Integer id) {
        if(id == null){
            return R.failMsg("id不能为空");
        }
        mapper.deleteById(id);
        template.opsForZSet().remove(TASK_REMIND, String.valueOf(id));
        return R.success();
    }

    @Override
    public R updateTask(Task task) {
        if(task.getId() == null){
            return R.failMsg("任务id不能为空");
        }
        if(task.getRemindTime() == null){
            return R.failMsg("任务提醒时间不能为空");
        }
        if(task.getDescription() == null){
            return R.failMsg("任务描述不能为空");
        }
        mapper.updateById(task);
        long remindMills = task.getRemindTime().getNano() / 1000;
        template.opsForZSet().add(TASK_REMIND, String.valueOf(task.getId()), remindMills);
        return R.success(task);
    }

    @Override
    public R getTaskByUserId(Integer userId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId);
        List<Task> tasks = mapper.selectList(wrapper);
        return R.success(tasks);
    }
}
