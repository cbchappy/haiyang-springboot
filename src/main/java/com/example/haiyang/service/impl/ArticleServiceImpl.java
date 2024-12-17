package com.example.haiyang.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.dao.ArticleMapper;
import com.example.haiyang.entity.Article;
import com.example.haiyang.service.IArticleService;
import com.example.haiyang.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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


    @Override
    public R getByType(Integer type) {
        String key = ARTICLE_TYPE + type;
        String s = template.opsForValue().get(key);
        if(s != null){
            List<Article> articles = JSON.parseArray(s, Article.class);
            return R.success(articles);
        }

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Article::getType, type);
        List<Article> list = list(wrapper);

        template.opsForValue().set(key, JSON.toJSONString(list));

        return R.success(list);
    }

    @Override
    public R addArticle(Article article) {
        if(article.getArticleUrl() == null || article.getImgUrl() == null
                || article.getType() == null || article.getTitle() == null){
            return R.fail();
        }
        save(article);

        Integer type = article.getType();
        template.delete(ARTICLE_TYPE + type);

        return R.success();
    }
}
