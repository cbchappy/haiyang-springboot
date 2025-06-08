package com.example.haiyang.controller;

import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.BaseComment;
import com.example.haiyang.entity.PostComment;
import com.example.haiyang.service.IBaseCommentService;
import com.example.haiyang.service.IPostCommentService;
import com.example.haiyang.util.Desc;
import com.example.haiyang.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@RestController
@RequestMapping("/postComment")
public class PostCommentController {
    @Autowired
    private IPostCommentService service;

    @Desc("添加帖子评论")
    @PostMapping("/add")
    public R addComment(@RequestBody PostComment comment){
        return service.addComment(comment);
    }

    @Desc("删除帖子评论")
    @DeleteMapping("/{id}")
    public R deleteComment(@PathVariable Integer id){
        return service.deleteComment(id);
    }

    @Desc("分页获取帖子评论")
    @PostMapping("/page/{postId}")
    public R getPage(@RequestBody PageDTO pageDTO, @PathVariable Integer postId){
        return service.getPage(pageDTO, postId);
    }
}
