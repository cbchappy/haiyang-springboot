package com.example.haiyang.controller;

import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.BaseComment;
import com.example.haiyang.service.IBaseCommentService;
import com.example.haiyang.service.ICommonService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2025-02-17
 */
@RestController
@RequestMapping("/baseComment")
@Slf4j
public class BaseCommentController {
    @Autowired
    private IBaseCommentService service;

    @Desc("添加基地评论")
    @PostMapping("/add")
    public R addComment(@RequestBody BaseComment comment){
        return service.addComment(comment);
    }
    //
    @Desc("删除基地评论")
    @DeleteMapping("/{id}")
    public R deleteComment(@PathVariable Integer id){
        return service.deleteComment(id);
    }

    //
    @Desc("分页获取基地评论")
    @PostMapping("/page/{baseId}")
    public R getPage(@RequestBody PageDTO pageDTO, @PathVariable Integer baseId){
        return service.getPage(pageDTO, baseId);
    }
}
