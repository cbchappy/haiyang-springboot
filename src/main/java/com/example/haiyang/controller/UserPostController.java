package com.example.haiyang.controller;

import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.UserPost;
import com.example.haiyang.service.IUserPostService;
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
@RequestMapping("/userPost")
public class UserPostController {
    @Autowired
    private IUserPostService service;

    @Desc("添加帖子")
    @PostMapping("/add")
    public R addPost(@RequestBody UserPost post){
        return service.addPost(post);
    }

    @Desc("删除帖子")
    @DeleteMapping("/{id}")
    public R deletePost(@PathVariable Integer id){
        return service.deletePost(id);
    }

    @Desc("修改帖子")
    @PostMapping("/update")
    public R updatePost(@RequestBody UserPost post){
        return service.updatePost(post);
    }

    @Desc("分页获取帖子")
    @PostMapping("/page")
    public R getPage(@RequestBody PageDTO dto){
        return service.getPage(dto);
    }

    //
    @Desc("获取用户所有帖子")
    @GetMapping("/user/all/{id}")
    public R getUserPost(@PathVariable  Integer id){
        return service.getUserPost(id);
    }

    @Desc("获取热点帖子")
    @GetMapping("/hot")
    public R getHotPost(){
        return service.getHotPost();
    }

    @Desc("给帖子点赞")
    @PostMapping("/like/{id}")
    public R like(@PathVariable Integer id){
        return service.like(id);
    }
    //取消帖子点赞
    @Desc("给帖子点赞")
    @PostMapping("/unlike/{id}")
    public R unlike(@PathVariable Integer id){
        return service.unlike(id);
    }
}
