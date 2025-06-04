package com.example.haiyang.common.aop;

import com.example.haiyang.util.CircuitBreaker;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Cbc
 * @DateTime 2025/2/15 15:00
 * @Description 方法级别熔断处理
 */
@Aspect
@Component
@Slf4j
public class FusingAop {
    private ConcurrentHashMap<String, CircuitBreaker> map = new ConcurrentHashMap<>();
    @Pointcut("execution(public * com.example.haiyang.controller.*.*(..))")
    public void fusingPointCut() {
    }

    @Around(value = "fusingPointCut()")
    public Object fusing(ProceedingJoinPoint joinPoint) {

        CircuitBreaker breaker = getCircuitBreaker(joinPoint);
        if(!breaker.isCanAccess()){
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getResponse();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        boolean isException = false;
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            isException = true;
            throw new RuntimeException(e);
        }finally {
            breaker.isResponse(isException);
        }
    }

    private CircuitBreaker getCircuitBreaker(ProceedingJoinPoint joinPoint){
        String className = joinPoint.getTarget().getClass().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String[] parameterNames = signature.getParameterNames();
        String key = className + "-" + methodName + "-" + Arrays.toString(parameterNames);
        CircuitBreaker breaker = map.get(key);
        if(breaker == null){
            synchronized (key){
                breaker = map.get(key);
                if(breaker == null){
                    breaker = new CircuitBreaker();
                    map.put(key, breaker);
                }
            }
        }
        return breaker;
    }
}
