package com.example.haiyang.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.Article;
import com.example.haiyang.entity.UserPost;
import com.example.haiyang.dao.UserPostMapper;
import com.example.haiyang.service.IUserPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import com.example.haiyang.util.RedisBloomFilter;
import com.example.haiyang.vo.PageVo;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.example.haiyang.common.constants.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@Service
public class UserPostServiceImpl extends ServiceImpl<UserPostMapper, UserPost> implements IUserPostService {
    private RedisBloomFilter bloomFilter;


    private final Cache<String, List<UserPost>> postCache = Caffeine.newBuilder()
            .maximumSize(1000) // 最大容量
            .expireAfterAccess(10, TimeUnit.MINUTES) // 写入后10分钟过期
            .build();

    @Autowired
    private UserPostMapper mapper;
    @Autowired
    private StringRedisTemplate template;

    @Override
    public R addPost(UserPost post) {
        if(post == null || post.getContent() == null ||post.getContent().equals("")){
            return R.failMsg("帖子内容不能为空!");
        }
        post.setUserId(MyThreadLocal.getUserId());
        post.setCreateTime(LocalDateTime.now());
        post.setLikeNum(0);
        mapper.insert(post);
        template.opsForZSet().add(HOT_POST_IDS, String.valueOf(post.getId()), 1);
        return R.success();
    }

    @Override
    public R deletePost(Integer id) {
        if(id == null){
            return R.failMsg("id不能为空");
        }
        //删除缓存
        mapper.deleteById(id);
        //如果是热点文章删除，也要更新热点文章
        Double score = template.opsForZSet().score(POST_IDS, id);
        if(score == null){
            return R.success();
        }
        template.opsForZSet().remove(POST_IDS, String.valueOf(id));
        Object v = template.opsForHash().get(HOT_POST_MAP, String.valueOf(id));
        if(v != null){
            buildHotPost();
        }
        return R.success();
    }

    @Override
    public R updatePost(UserPost post) {
        if(post.getId() == null){
            return R.failMsg("文章id不能为空");
        }
        String content = post.getContent();
        if(content == null || content.equals("")){
            return R.failMsg("文章内容不能为空!");
        }
        mapper.updateById(post);
        Object v = template.opsForHash().get(HOT_POST_MAP, String.valueOf(post.getId()));
        if(v == null){
            return R.success();
        }
        buildHotPost();
        return R.success();
    }

    @Override
    public R getPage(PageDTO dto) {
        LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(UserPost::getId, dto.getLastId());
        wrapper.orderByDesc(UserPost::getId);
        Page<UserPost> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        page = mapper.selectPage(page, wrapper);
        List<UserPost> records = page.getRecords();
        PageVo vo = new PageVo();
        vo.setTotal((int) page.getTotal());
        vo.setPageNum(dto.getPageNum());
        vo.setList(records);
        if(records == null || records.size() == 0){
            vo.setLastIndex(dto.getLastId());
        }else {
            vo.setLastIndex(records.get(records.size() - 1).getId());
        }
        return R.success(vo);
    }

    @Override
    public R getUserPost(Integer userId) {
        if(userId == null){
            return R.failMsg("用户id不能为空!");
        }
        LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPost::getUserId, userId);
        return R.success(mapper.selectList(wrapper));
    }

    @Override
    public R getHotPost() {
        List<UserPost> userPosts = postCache.asMap().get(HOT_POST_MAP);
        if(userPosts != null){
            return R.success(userPosts);
        }else {
            userPosts = new ArrayList<>();
        }
        Map<Object, Object> map = template.opsForHash().entries(HOT_POST_MAP);
        if(map.size() == 0){
            buildHotPost();
            map = template.opsForHash().entries(HOT_POST_MAP);
        }
        List<UserPost> finalUserPosts = userPosts;
        map.forEach((o, v) -> {
            String s = (String) v;
            UserPost post = JSON.parseObject(s, UserPost.class);
            finalUserPosts.add(post);
        });
        postCache.put(HOT_POST_MAP, finalUserPosts);
        return R.success(finalUserPosts);
    }

    @Override
    public R like(Integer id) {
        if(id == null){
            return R.failMsg("id不能为空");
        }
        if(!bloomFilter.mightContain(String.valueOf(id))){
            return R.failMsg("id错误");
        }
        Integer userId = MyThreadLocal.getUserId();
        String key = USER_LIKE_POST + userId;
        //若已点赞，直接返回
        if(Boolean.TRUE.equals(template.opsForSet().isMember(key, String.valueOf(id)))){
            return R.failMsg("不能重复点赞");
        }
        template.opsForSet().add(key, String.valueOf(id));

        Double score = template.opsForZSet().score(POST_IDS, String.valueOf(id));
        double num;
        if(score == null){
            LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(UserPost::getLikeNum);
            UserPost post = mapper.selectOne(wrapper);
            if(post == null || post.getLikeNum() == null){
                template.opsForSet().remove(key, String.valueOf(id));
                return R.failMsg("id错误");
            }
            num = (double) (post.getLikeNum() + 1);
        }else {
            num = score + 1;
        }
        template.opsForZSet().add(POST_IDS, String.valueOf(id), num);

        return R.success();
    }

    @Override
    public R unlike(Integer id) {
        if(id == null){
            return R.failMsg("id不能为空");
        }
        if(!bloomFilter.mightContain(String.valueOf(id))){
            return R.failMsg("id错误");
        }
        Integer userId = MyThreadLocal.getUserId();
        String key = USER_LIKE_POST + userId;
        //若未点赞，直接返回
        if(Boolean.FALSE.equals(template.opsForSet().isMember(key, String.valueOf(id)))){
            return R.failMsg("未点赞，不能取消点赞");
        }
        template.opsForSet().remove(key, String.valueOf(id));
        Double score = template.opsForZSet().score(POST_IDS, String.valueOf(id));
        double num;
        if(score == null){
            LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(UserPost::getLikeNum);
            UserPost post = mapper.selectOne(wrapper);
            if(post == null || post.getLikeNum() == null){
                return R.failMsg("id错误");
            }
            num = (double) (post.getLikeNum() - 1);
        }else {
            num = score - 1;
        }
        template.opsForZSet().add(POST_IDS, String.valueOf(id), num);

        return R.success();
    }

    public void buildHotPost(){
        Set<String> top = template.opsForZSet().reverseRange(POST_IDS, 0, 9);
        Map<String, String> map = new HashMap<>();
        if (top != null) {
            for (String s : top) {
                Integer id = Integer.parseInt(s);
                UserPost post = mapper.selectById(id);
                map.put(s, JSON.toJSONString(post));
            }
        }
        template.opsForHash().putAll(HOT_POST_MAP, map);
    }

    @PostConstruct
    public void init() {
        bloomFilter = new RedisBloomFilter(template, BLOOM_USER_POST, 10000, 0.01);

        List<UserPost> list = list();
        for (UserPost post : list) {
            bloomFilter.mightContain(String.valueOf(post.getId()));
        }

    }
}
