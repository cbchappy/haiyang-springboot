package com.example.haiyang.listener;

import cn.hutool.json.JSONUtil;
import com.example.haiyang.constants.KafkaConstants;
import com.example.haiyang.entity.ExceptionLog;
import com.example.haiyang.entity.OperateLog;
import com.example.haiyang.service.IExceptionLogService;
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
    private IOperateLogService operateLogService;

    @Autowired
    private IExceptionLogService exceptionLogService;

    @KafkaListener(topics = KafkaConstants.OPERATE_LOG_TOPIC)
    public void onMsgOperateLog(String msg){
        log.debug("存储操作日志");
        OperateLog operateLog = JSONUtil.toBean(msg, OperateLog.class);
        operateLogService.save(operateLog);
    }

    @KafkaListener(topics = KafkaConstants.EXCEPTION_LOG_TOPIC)
    public void onMsgExceptionLog(String msg){
        log.debug("存储操作错误日志");
        ExceptionLog bean = JSONUtil.toBean(msg, ExceptionLog.class);
        exceptionLogService.save(bean);
    }
}
