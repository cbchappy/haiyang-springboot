package com.example.haiyang.common.schedule;

import com.alibaba.fastjson.JSON;
import com.example.haiyang.entity.UserPost;
import com.example.haiyang.service.IUserPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.example.haiyang.common.constants.KafkaConstants.POST_LIKE_NUM_TOPIC;
import static com.example.haiyang.common.constants.RedisConstants.POST_IDS;
import static com.example.haiyang.common.constants.RedisConstants.TASK_REMIND;

/**
 * @Author Cbc
 * @DateTime 2025/2/18 15:21
 * @Description
 */
@Component
public class ScheduledTasks {
    @Autowired
    IUserPostService postService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;


    //构建热帖缓存
    @Scheduled(cron = "0 0 * * * ?")
    public void buildHotPost() {
        postService.buildHotPost();
    }

    @Scheduled(cron = "0 * * * * ?")
    public void likeNumToKafka() {
        Set<ZSetOperations.TypedTuple<String>> membersWithScores = redisTemplate.opsForZSet()
                .rangeWithScores(POST_IDS, 0, -1);
        if (membersWithScores != null) {
            for (ZSetOperations.TypedTuple<String> mb : membersWithScores) {
                Double score = mb.getScore();
                String value = mb.getValue();
                UserPost post = null;
                if (value != null && score != null) {

                    post = UserPost.builder()
                            .id(Integer.valueOf(value))
                            .likeNum(score.intValue())
                            .build();

                }
                kafkaTemplate.send(POST_LIKE_NUM_TOPIC, JSON.toJSONString(post));
            }
        }
    }

    @Scheduled(cron = "0,30 * * * * ?")
    public void taskToKafka(){
        Set<String> set = redisTemplate.opsForZSet().rangeByScore(TASK_REMIND, 0, System.currentTimeMillis());
        if (set != null) {
            for (String s : set) {
                kafkaTemplate.send(TASK_REMIND, s);
                redisTemplate.opsForZSet().remove(TASK_REMIND, s);
            }
        }
    }

}
