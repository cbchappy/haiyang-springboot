package com.example.haiyang.common.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.example.haiyang.common.constants.KafkaConstants;

import com.example.haiyang.entity.Chat;
import com.example.haiyang.entity.OperateLog;
import com.example.haiyang.entity.Task;
import com.example.haiyang.entity.UserPost;
import com.example.haiyang.service.IChatService;
import com.example.haiyang.service.IOperateLogService;
import com.example.haiyang.service.ITaskService;
import com.example.haiyang.service.IUserPostService;
import com.example.haiyang.util.MyWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Author Cbc
 * @DateTime 2024/11/17 15:04
 * @Description
 */
@Component
@Slf4j
public class MyKafkaListener {

    @Autowired
    private IOperateLogService service;

    @Autowired
    private IChatService chatService;

    @Autowired
    private IUserPostService postService;

    @Autowired
    private ITaskService taskService;

    @KafkaListener(topics = KafkaConstants.OPERATE_LOG_TOPIC)
    public void onMsgOperateLog(String msg){
        log.info("kafka存储操作日志");
        OperateLog operateLog = JSONUtil.toBean(msg, OperateLog.class);
        service.save(operateLog);
    }

    @KafkaListener(topics = KafkaConstants.AI_TOPIC)
    public void onMsgChat(String msg){
        log.info("kafka存储AI对话信息");
        Chat chat = JSONUtil.toBean(msg, Chat.class);
        chatService.save(chat);
    }

    @KafkaListener(topics = KafkaConstants.POST_LIKE_NUM_TOPIC)
    public void onPostLikeNum(String msg){
        UserPost post = JSON.parseObject(msg, UserPost.class);
        postService.updatePost(post);
    }
    @KafkaListener(topics = KafkaConstants.TASK_TOPIC)
    public void onTask(String id){
        Task task = taskService.getById(id);
        MyWebSocket.sendTask(task.getId(), task.getUserId());
    }


}
