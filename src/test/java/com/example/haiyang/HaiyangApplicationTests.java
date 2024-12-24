package com.example.haiyang;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.example.haiyang.util.BigModel;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static com.example.haiyang.constants.RedisConstants.AI_HISTORY;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HaiyangApplicationTests {


    @Test
    void contextLoads() {
        StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
       String key = AI_HISTORY + "1";
       redisTemplate.delete(key);
    }

}
