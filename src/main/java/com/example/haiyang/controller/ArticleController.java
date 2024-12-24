package com.example.haiyang.controller;

import com.example.haiyang.entity.Article;
import com.example.haiyang.service.IArticleService;
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

    @GetMapping("/{type}")
    public R getByType(@PathVariable Integer type){
        log.info("根据类型获取文章");
        return service.getByType(type);
    }

    @PostMapping("/add")
    public R addArticle(@RequestBody Article article){
        log.info("添加文章");
        return service.addArticle(article);
    }

}
