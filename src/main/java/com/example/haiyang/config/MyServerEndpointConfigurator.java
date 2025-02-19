package com.example.haiyang.config;


import com.example.haiyang.constants.RedisConstants;
import com.example.haiyang.constants.RequestConstants;
import com.example.haiyang.util.MyThreadLocal;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Cbc
 * @DateTime 2024/11/26 19:11
 * @Description websocket配置 添加握手期间处理
 */
@Component
@Slf4j
public class MyServerEndpointConfigurator extends ServerEndpointConfig.Configurator{

    private static StringRedisTemplate template;

    @Autowired
    public void setTemplate(StringRedisTemplate template) {
        MyServerEndpointConfigurator.template = template;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {


        Map<String, List<String>> headers = request.getHeaders();
        String token = headers.get(RequestConstants.HEADER_TOKEN).get(0);
        String userId = headers.get(RequestConstants.HEADER_USERID).get(0);

        log.info("请求:{}-进入了websocket拦截器!", request.getRequestURI());


        userId = userId == null ? "" : userId;
        String reToken = template.opsForValue().getAndExpire(RedisConstants.LOGIN + userId, 30, TimeUnit.MINUTES);

        if (reToken == null || !reToken.equals(token)) {
            log.info("请求:{}-websocket权限校验不通过", request.getRequestURI());
            throw new RuntimeException("websocket请求无法通过权限校验");
        }
        MyThreadLocal.setUserId(Integer.valueOf(userId));

        super.modifyHandshake(sec, request, response);
    }
}
