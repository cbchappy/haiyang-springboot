package com.example.haiyang.interceptor;

import cn.hutool.json.JSONUtil;
import com.example.haiyang.constants.RequestConstants;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import com.example.haiyang.constants.RedisConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.TimeUnit;

/**
 * @Author Cbc
 * @DateTime 2024/10/16 21:08
 * @Description 自定义拦截器 进行权限校验 并存储一些信息到ThreadLocal
 */
@Slf4j
@Component
public class MyInterceptor implements HandlerInterceptor {

    private static StringRedisTemplate template;

    @Autowired
    public void setTemplate(StringRedisTemplate template) {
        MyInterceptor.template = template;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("请求{}进入了拦截器!", request.getRequestURI());
        String token = request.getHeader(RequestConstants.HEADER_TOKEN);
        String userId = request.getHeader(RequestConstants.HEADER_USERID);
         userId = userId == null ? "" : userId;
        String reToken = template.opsForValue().getAndExpire(RedisConstants.LOGIN + userId, 30, TimeUnit.MINUTES);

        if (reToken == null || !reToken.equals(token)) {
            log.info("权限校验不通过, 拒绝访问");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(R.fail("没有权限", null)));
            response.setContentType("application/json");
            return false;
        }
        MyThreadLocal.setUserId(Integer.valueOf(userId));
        MyThreadLocal.setToken(reToken);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
