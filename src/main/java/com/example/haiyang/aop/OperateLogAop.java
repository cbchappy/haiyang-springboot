package com.example.haiyang.aop;

import cn.hutool.json.JSONUtil;
import com.example.haiyang.constants.KafkaConstants;

import com.example.haiyang.entity.OperateLog;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.MyUtil;
import com.example.haiyang.util.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;


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

        // 获取RequestAttributes
        OperateLog operateLog = new OperateLog();
        operateLog.setUserId(MyThreadLocal.getUserId());//userId
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            improveOperateLog(requestAttributes.getRequest(), operateLog);
        }
        operateLog.setCreateTime(LocalDateTime.now());
        Signature signature = joinPoint.getSignature();
        operateLog.setClassName(joinPoint.getTarget().getClass().getName());
        operateLog.setMethodName(signature.getName());
        Object[] args = joinPoint.getArgs();
        if(args != null){
            String[] methodType = new String[args.length];
            for (int i = 0; i < methodType.length; i++) {
                methodType[i] = args[i].getClass().getName();
            }
            operateLog.setParamType(JSONUtil.toJsonStr(methodType));
        }


        long start = System.currentTimeMillis();
        Throwable e = null;

        Object res = null;
        try {
            res = joinPoint.proceed();
        } catch (Throwable t) {
            e = t;
            throw new RuntimeException(e);
        }finally {
            recordLog(e, operateLog, start, res);
        }

        return res;
    }

    private static void improveOperateLog(HttpServletRequest request, OperateLog operateLog){
        operateLog.setRequestMethod(request.getMethod());
        operateLog.setRequestParams(JSONUtil.toJsonStr(request.getParameterMap()));
        operateLog.setRequestUrl(request.getRequestURL().toString());

        try {
            BufferedReader reader = request.getReader();
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);
            operateLog.setRequestBody(builder.toString());
        } catch (Exception ignored) {

        }
    }

    private void recordLog(Throwable e, OperateLog operateLog, long start, Object res){
        long end = System.currentTimeMillis();
        operateLog.setConsumeTime((int) (end - start));
        if(e != null){
            operateLog.setResType(OperateLog.ResTypeCode.exception);
            res = MyUtil.getThrowableContent(e);
        }else {
            R r = (R) res;
            if(r.getCode() == R.Code.success){
                operateLog.setResType(OperateLog.ResTypeCode.success);
            }else {
                operateLog.setResType(OperateLog.ResTypeCode.fail);
            }
        }
        String jsonStr = JSONUtil.toJsonStr(res);
        operateLog.setResult(jsonStr);
        String opLog = JSONUtil.toJsonStr(operateLog);

        log.info("\n详细请求信息日志:{}", opLog);

        kafkaTemplate.send(KafkaConstants.OPERATE_LOG_TOPIC, opLog);
    }








//    @AfterThrowing(value = "operExceptionLogPoinCut()", throwing = "e", argNames = "joinPoint,e")//todo !!!!!!!!!
//    //todo 方法名顺序要正常
//    public void ExceptionRecord(JoinPoint joinPoint, Throwable e) {
//
//
//    }


}
