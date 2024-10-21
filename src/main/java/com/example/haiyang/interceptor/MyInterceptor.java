package com.example.haiyang.interceptor;

import cn.hutool.Hutool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.haiyang.util.CommonProperties;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import com.example.haiyang.util.RedisName;
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
 * @Description
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
        log.info("{}进入了拦截器!", request.getRequestURI());
        String token = request.getHeader(CommonProperties.TOKEN_NAME);
        token = token == null ? "" : token;
        String id = template.opsForValue().getAndExpire(RedisName.LOGIN_EXPIRE + token, 30, TimeUnit.MINUTES);

        if (StrUtil.isBlankIfStr(id)) {
            log.info("拒绝访问");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(R.fail("没有权限", null)));
            response.setContentType("application/json");
            return false;
        }
        MyThreadLocal.setUserId(Integer.valueOf(id));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
