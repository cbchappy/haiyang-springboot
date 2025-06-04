package com.example.haiyang.common.interceptor;

import cn.hutool.json.JSONUtil;
import com.example.haiyang.common.constants.RequestConstants;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import com.example.haiyang.common.constants.RedisConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.example.haiyang.common.constants.RedisConstants.*;

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
        String uri = request.getRequestURI();
        String method = request.getMethod();
        log.info("请求 {}:{} 进入了拦截器!", method, uri);
        String token = request.getHeader(RequestConstants.HEADER_TOKEN);
        String userId = request.getHeader(RequestConstants.HEADER_USERID);
         userId = userId == null ? "" : userId;
        String reToken = template.opsForValue().getAndExpire(RedisConstants.LOGIN + userId, 30, TimeUnit.MINUTES);

        if (reToken == null || !reToken.equals(token)) {
            log.info("请求{}权限校验不通过, 拒绝访问", uri);
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(R.failMsg("没有权限")));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            return false;
        }
        if(template.opsForValue().get(BLACKLIST + userId) != null){
            log.info("用户在黑名单中");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        if(!checkFrequency(userId)){
            template.opsForValue().set(BLACKLIST + userId, userId);
            template.expire(BLACKLIST + userId, 10, TimeUnit.MINUTES);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        MyThreadLocal.setUserId(Integer.valueOf(userId));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MyThreadLocal.removeUserId();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 检查用户请求频率异常
     * @param userId
     *
     */
    private boolean checkFrequency(String userId){
        //时间 数量 数量 数量 数量 数量  限时
        String key = USER_FREQUENCY_TIME + userId;
        String listKey = USER_FREQUENCY_LIST + userId;
        String s = template.opsForValue().get(key);
        long now = System.currentTimeMillis() / 1000;
        template.opsForValue().set(key, String.valueOf(now));
        if(s == null){
            for(int i = 0; i < 4; i++){
                template.opsForList().rightPush(listKey, String.valueOf(0));
            }
            template.opsForList().rightPush(listKey, String.valueOf(1));
            return true;
        }
        long pre = Long.parseLong(s);
        if(now - pre >= 5){
            for(int i = 0; i < 4; i++){
                template.opsForList().rightPush(listKey, String.valueOf(0));
            }
            template.opsForList().rightPush(listKey, String.valueOf(1));
            return true;
        }
        int sub = (int) (now - pre);
        List<String> list = template.opsForList().leftPop(listKey, 5);
        if(sub == 0){
            String ms = list.get(list.size() - 1);
            ms = String.valueOf(Integer.parseInt(ms) + 1);
            list.set(list.size() - 1, ms);
            template.opsForList().rightPop(listKey);
            template.opsForList().rightPush(listKey, ms);
        }else {
            for(int i = 0; i < sub; i++){
                template.opsForList().leftPop(listKey);
                list.remove(0);
                String nv = String.valueOf(i == sub - 1 ? 1 : 0);
                list.add(nv);
                template.opsForList().rightPush(listKey, nv);
            }
        }

        final int[] v = {0};
        list.forEach(s1 -> v[0] += Integer.parseInt(s1));
        return v[0] <= 5 * 3;
    }
}
