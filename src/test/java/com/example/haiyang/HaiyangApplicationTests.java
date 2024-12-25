package com.example.haiyang;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.haiyang.util.BigModel;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static com.example.haiyang.constants.RedisConstants.AI_HISTORY;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HaiyangApplicationTests {


    @Test
    void contextLoads() {
        StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
       String key = AI_HISTORY + "1";
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        BigModel.RoleContent roleContent = new BigModel.RoleContent();
        roleContent.setContent("odoodoq");
        roleContent.setRole("user");
        opsForList.rightPush(key, JSON.toJSONString(roleContent));
        Long size = opsForList.size(key);
        List<String> range = opsForList.range(key, 0, size - 1);
        System.out.println(range);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(range.get(0));
        jsonArray.add(JSON.toJSON(range.get(1)));
        jsonArray.fluentAdd(range.get(0));
        System.out.println(jsonArray.toJSONString());
    }



}
