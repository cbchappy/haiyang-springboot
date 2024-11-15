package com.example.haiyang.exceptionHandler;

import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Cbc
 * @DateTime 2024/11/12 15:37
 * @Description
 */
@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public R allException(Exception exception){
        String msg = exception.getCause() == null ? exception.getMessage() : exception.getCause().getMessage();
        log.error("出现异常:{}", msg);
        return R.fail("服务器异常:" + msg, null);
    }
}
