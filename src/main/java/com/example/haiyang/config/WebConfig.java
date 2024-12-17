package com.example.haiyang.config;

import com.example.haiyang.interceptor.MyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author Cbc
 * @DateTime 2024/10/16 21:13
 * @Description mvc拦截器 拦截controller的请求
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor()).excludePathPatterns("/user/login", "/user/signIn", "/test/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }


}
