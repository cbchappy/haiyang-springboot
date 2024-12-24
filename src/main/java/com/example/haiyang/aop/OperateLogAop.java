package com.example.haiyang.aop;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.example.haiyang.constants.KafkaConstants;
import com.example.haiyang.entity.ExceptionLog;
import com.example.haiyang.entity.OperateLog;
import com.example.haiyang.service.IExceptionLogService;
import com.example.haiyang.service.IOperateLogService;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.MyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @Author Cbc
 * @DateTime 2024/11/15 20:05
 * @Description 记录操作日志和异常日志
 */
@Aspect
@Component
@Slf4j
public class OperateLogAop {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Pointcut("execution(public * com.example.haiyang.controller.*.*(..))")
    public void operExceptionLogPoinCut() {

    }


    @Around(value = "operExceptionLogPoinCut()")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("aop代理, 记录操作日志");
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
        kafkaTemplate.send(KafkaConstants.OPERATE_LOG_TOPIC, JSONUtil.toJsonStr(operateLog));
        return res;
    }


    @AfterThrowing(value = "operExceptionLogPoinCut()", throwing = "e", argNames = "joinPoint,e")//todo !!!!!!!!!
    //todo 方法名顺序要正常
    public void ExceptionRecord(JoinPoint joinPoint, Throwable e) {

        log.debug("aop代理, 记录操作异常日志");

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setUserId(MyThreadLocal.getUserId());//用户id
        exceptionLog.setCreateTime(LocalDateTime.now());//创建时间

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        exceptionLog.setUrl(request.getRequestURL().toString());//url
        exceptionLog.setParams(JSONUtil.toJsonStr(request.getParameterMap()));//参数
        exceptionLog.setRequestMethod(request.getMethod());//请求方式
        // 获取请求体
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            exceptionLog.setRequestBody(content.toString());//请求体--json模式 不包括文件
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        exceptionLog.setExceptionName(e.getClass().getName());//exception名
        String content = MyUtil.getThrowableContent(e);
        exceptionLog.setExceptionContent(content);//exception具体信息
        exceptionLog.setOperateMethod(joinPoint.getSignature().getName());//方法名

        kafkaTemplate.send(KafkaConstants.EXCEPTION_LOG_TOPIC, JSONUtil.toJsonStr(exceptionLog));

    }


}
//id userId content create_time status websocket
//exception_log ---> id userId exception msg class method params create_time