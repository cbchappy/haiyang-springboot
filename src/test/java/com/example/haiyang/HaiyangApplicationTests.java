package com.example.haiyang;

import com.example.haiyang.constants.RedisConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HaiyangApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set(RedisConstants.LOGIN + "1", "a");
    }

}
