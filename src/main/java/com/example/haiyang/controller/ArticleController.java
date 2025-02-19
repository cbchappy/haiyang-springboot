package com.example.haiyang.controller;

import com.example.haiyang.entity.Article;
import com.example.haiyang.service.IArticleService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2024-12-16
 */
@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {

    @Autowired
    private IArticleService service;

    @Desc("根据类型获取文章")
    @GetMapping("/{type}")
    public R getByType(@PathVariable Integer type){
        log.info("userId:{}-根据类型获取文章", MyThreadLocal.getUserId());
        return service.getByType(type);
    }
    @Desc("添加文章")
    @PostMapping("/add")
    public R addArticle(@RequestBody Article article){
        log.info("userId:{}-添加文章", MyThreadLocal.getUserId());
        return service.addArticle(article);
    }

}
