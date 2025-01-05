package com.example.haiyang.listener;

import cn.hutool.json.JSONUtil;
import com.example.haiyang.constants.KafkaConstants;

import com.example.haiyang.entity.OperateLog;
import com.example.haiyang.service.IOperateLogService;
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

    @KafkaListener(topics = KafkaConstants.OPERATE_LOG_TOPIC)
    public void onMsgOperateLog(String msg){
        log.info("kafka存储操作日志");
        OperateLog operateLog = JSONUtil.toBean(msg, OperateLog.class);
        service.save(operateLog);
    }


}
