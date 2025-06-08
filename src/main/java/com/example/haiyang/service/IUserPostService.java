package com.example.haiyang.service;

import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.UserPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.util.R;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
public interface IUserPostService extends IService<UserPost> {

    R addPost(UserPost post);

    R deletePost(Integer id);

    R updatePost(UserPost post);

    R getPage(PageDTO dto);

    R getUserPost(Integer userId);

    R getHotPost();

    R like(Integer id);

    R unlike(Integer id);

    void buildHotPost();
}
