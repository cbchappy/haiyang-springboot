package com.example.haiyang.exceptionHandler;

import com.example.haiyang.util.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * @Author Cbc
 * @DateTime 2024/11/12 15:37
 * @Description
 */
@RestControllerAdvice
@Slf4j
@Order(value = 2)
public class MyExceptionHandler {

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                      HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址 {},不支持 {} 请求", requestURI, e.getMethod());
        return R.fail("请求方式不支持:" + request.getMethod(), null);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        log.error("请求地址 {},异常: {}", requestURI, String.valueOf(e));

       return R.fail("服务器出现运行时异常:" + e.getMessage(), null);
    }


    @ExceptionHandler(value = Exception.class)
    public R allException(Exception exception){
        String msg = exception.getMessage();
        log.error("出现异常:{}", msg);
        return R.fail("服务器出现系统异常:" + msg, null);
    }
}
