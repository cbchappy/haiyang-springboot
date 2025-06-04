package com.example.haiyang.common.filter;


import com.example.haiyang.util.MyRateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author Cbc
 * @DateTime 2025/2/15 15:43
 * @Description 限流过滤器
 */
@Component
@Order(1)
public class LimitFilter implements Filter {
    private final MyRateLimiter limiter = new MyRateLimiter(100, 10, 3);
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       if(limiter.tryAcquire(1)){
           chain.doFilter(request, response);
           return;
       }
        HttpServletResponse httpRes = (HttpServletResponse) response;
       httpRes.sendError(429, "服务器限流");
    }
}
