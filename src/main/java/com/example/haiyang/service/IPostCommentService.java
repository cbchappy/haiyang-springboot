package com.example.haiyang.service;

import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.PostComment;
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
public interface IPostCommentService extends IService<PostComment> {

    R addComment(PostComment comment);

    R deleteComment(Integer id);

    R getPage(PageDTO pageDTO, Integer postId);
}
