package com.example.haiyang.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.dao.ArticleMapper;
import com.example.haiyang.dto.ArticlePageDTO;
import com.example.haiyang.entity.Article;
import com.example.haiyang.service.IArticleService;
import com.example.haiyang.util.R;
import com.example.haiyang.util.RedisBloomFilter;
import com.example.haiyang.vo.ArticlePageVO;
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

    @Override
    public R getPage(ArticlePageDTO dto) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Article::getId, dto.getLastId());
        wrapper.orderByDesc(Article::getId);
        Page<Article> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        ArticlePageVO vo = new ArticlePageVO();
        List<Article> records = page.getRecords();
        vo.setArticleList(records);
        vo.setTotal(records.size());
        vo.setLastIndex(records.get(records.size() - 1).getId());
        vo.setPageNum(dto.getPageNum());
        return R.success(vo);
    }

    @Override
    public R getArticleById(Integer id) {
        if(id == null){
            return R.failMsg("文章id不能为空!");
        }
        return R.success(mapper.selectById(id));
    }

    @Override
    public R deleteById(Integer id) {
        if(id == null){
            return R.failMsg("文章id不能为空!");
        }
        Article article = mapper.selectById(id);
        if(article == null){
            return R.failMsg("文章id错误!");
        }
        String key = ARTICLE_TYPE + article.getType();
        mapper.deleteById(article);
        articleCache.asMap().remove(key);
        template.delete(key);
        return R.success();
    }

    @Override
    public R update(Article article) {
        if(article.getId() == null){
            return R.failMsg("文章id不能为空!");
        }
        Integer type = article.getType();
        if(type == null){
            return R.failMsg("文章类型不能为空");
        }
        updateById(article);
        String key = ARTICLE_TYPE + article.getType();
        articleCache.asMap().remove(key);
        template.delete(key);
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
