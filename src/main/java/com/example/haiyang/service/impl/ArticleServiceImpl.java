package com.example.haiyang.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.dao.ArticleMapper;
import com.example.haiyang.entity.Article;
import com.example.haiyang.service.IArticleService;
import com.example.haiyang.util.R;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonRxClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.haiyang.constants.RedisConstants.ARTICLE_LOCK;
import static com.example.haiyang.constants.RedisConstants.ARTICLE_TYPE;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2024-12-16
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Autowired
    StringRedisTemplate template;

    @Autowired
    ArticleMapper mapper;

    @Autowired
    RedissonClient redissonClient;


    @Override
    public R getByType(Integer type) {

        RLock rLock = redissonClient.getReadWriteLock(ARTICLE_LOCK + type).readLock();

        rLock.lock();

        List<Article> list;
        String key = ARTICLE_TYPE + type;
        try {
            String s = template.opsForValue().get(key);
            if(s != null){
                List<Article> articles = JSON.parseArray(s, Article.class);
                return R.success(articles);
            }
        } finally {
            rLock.unlock();
        }

        RLock writeLock = redissonClient.getReadWriteLock(ARTICLE_LOCK + type).writeLock();
        try {
            writeLock.lock();
            //redis缓存
            LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(type != null, Article::getType, type);
            list = list(wrapper);

            template.opsForValue().set(key, JSON.toJSONString(list));
        } finally {
            writeLock.unlock();
        }


        return R.success(list);
    }

    @Override
    public R addArticle(Article article) {

        if(article.getArticleUrl() == null || article.getImgUrl() == null
                || article.getType() == null || article.getTitle() == null){
            return R.failMsg("文章内容不完整!");
        }
        RLock writeLock = redissonClient.getReadWriteLock(ARTICLE_LOCK + article.getType()).writeLock();

        try {
            writeLock.lock();
            save(article);

            Integer type = article.getType();
            //移除redis缓存
            template.delete(ARTICLE_TYPE + type);
        } finally {
            writeLock.unlock();
        }

        return R.success();
    }
}
