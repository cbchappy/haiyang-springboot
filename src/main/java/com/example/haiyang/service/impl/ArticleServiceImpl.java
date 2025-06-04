package com.example.haiyang.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.dao.ArticleMapper;
import com.example.haiyang.entity.Article;
import com.example.haiyang.service.IArticleService;
import com.example.haiyang.util.R;
import com.example.haiyang.util.RedisBloomFilter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.haiyang.common.constants.RedisConstants.ARTICLE_TYPE;

/**
 * <p>
 * 服务实现类
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


    private Cache<String, List<Article>> articleCache = Caffeine.newBuilder()
            .maximumSize(10000) // 最大容量
            .expireAfterAccess(10, TimeUnit.MINUTES) // 写入后10分钟过期
            .build();

    private RedisBloomFilter bloomFilter;


    @Override
    public R getByType(Integer type) {
        if(!bloomFilter.mightContain(String.valueOf(type))){
            return R.success(null);
        }

        String key = ARTICLE_TYPE + type;
        List<Article> list;
        list = articleCache.getIfPresent(key);
        if(list != null){
            return R.success(list);
        }

        String s = template.opsForValue().get(key);
        if (s != null) {
            List<Article> articles = JSON.parseArray(s, Article.class);
            articleCache.put(key, articles);
            return R.success(articles);
        }



        //redis缓存
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Article::getType, type);
        list = list(wrapper);

        template.opsForValue().set(key, JSON.toJSONString(list));
        articleCache.put(key, list);


        return R.success(list);
    }

    @Override
    public R addArticle(Article article) {

        if (article.getArticleUrl() == null || article.getImgUrl() == null
                || article.getType() == null || article.getTitle() == null) {
            return R.failMsg("文章内容不完整!");
        }

        save(article);

        Integer type = article.getType();
        //移除redis缓存 和另一个缓存
        String key = ARTICLE_TYPE + type;
        template.delete(key);
        articleCache.asMap().remove(key);
        bloomFilter.add(String.valueOf(type));

        return R.success();
    }

    @PostConstruct
    public void init() {
        bloomFilter = new RedisBloomFilter(template, ARTICLE_TYPE + "bloom", 10000, 0.01);
        List<Article> list = list();
        for (Article article : list) {
            bloomFilter.mightContain(String.valueOf(article.getType()));
        }

    }
}
