package com.example.haiyang.controller;

import com.example.haiyang.dto.ArticlePageDTO;
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
    @GetMapping("/type/{type}")
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
    @Desc("分页获取文章")
    @PostMapping("/page")
    public R getPage(@RequestBody ArticlePageDTO pageDTO){
        log.info("userId:{}-分页查询文章", MyThreadLocal.getUserId());
        return service.getPage(pageDTO);
    }
    @Desc("根据Id获取文章")
    @GetMapping("/id/{id}")
    public R getById(@PathVariable Integer id){
        log.info("userId:{}-根据Id获取文章", MyThreadLocal.getUserId());
        return service.getArticleById(id);
    }

    @Desc("根据Id删除文章")
    @DeleteMapping("/id/{id}")
    public R delById(@PathVariable Integer id){
        log.info("userId:{}-根据Id删除文章", MyThreadLocal.getUserId());
        return service.deleteById(id);
    }
    @Desc("更新文章信息")
    @PostMapping("/update")
    public R update(@RequestBody Article article){
        log.info("userId:{}-更新文章信息", MyThreadLocal.getUserId());
        return service.update(article);
    }
}
