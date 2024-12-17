package com.example.haiyang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.entity.Article;
import com.example.haiyang.util.R;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2024-12-16
 */
public interface IArticleService extends IService<Article> {

    R getByType(Integer type);

    R addArticle(Article article);
}
