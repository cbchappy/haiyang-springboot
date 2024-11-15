package com.example.haiyang.aop;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.example.haiyang.entity.OperateLog;
import com.example.haiyang.service.IOperateLogService;
import com.example.haiyang.util.MyThreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author Cbc
 * @DateTime 2024/11/15 20:05
 * @Description
 */
@Aspect
@Component
public class OperateLogAop {

    @Autowired
    private IOperateLogService operateLogService;

    @Around(value = "@annotation(com.example.haiyang.annotation.RecordOperateLog)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        OperateLog operateLog = new OperateLog();
        operateLog.setUserId(MyThreadLocal.getUserId());
        operateLog.setOperateTime(LocalDateTime.now());
        operateLog.setOperateClass(joinPoint.getTarget().getClass().getName());//操作类名
        operateLog.setOperateMethod(joinPoint.getSignature().getName());//操作方法名
        operateLog.setOperateParameters(JSONUtil.toJsonStr(joinPoint.getArgs()));//操作参数
        long start = System.currentTimeMillis();

        Object res = joinPoint.proceed();

        long end = System.currentTimeMillis();
        operateLog.setConsumingTime(end - start);//花费的时间
        operateLog.setRes(JSONUtil.toJsonStr(res));
        //todo  将日志插入数据库 考虑结合异步 kafka或rabbitmq  mysql和redis数据卷挂载
        operateLogService.save(operateLog);
        return res;
    }
}
