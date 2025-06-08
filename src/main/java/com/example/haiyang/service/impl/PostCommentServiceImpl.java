package com.example.haiyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.BaseComment;
import com.example.haiyang.entity.PostComment;
import com.example.haiyang.dao.PostCommentMapper;
import com.example.haiyang.service.IPostCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.R;
import com.example.haiyang.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements IPostCommentService {

    @Autowired
    private PostCommentMapper mapper;
    @Override
    public R addComment(PostComment comment) {
        mapper.insert(comment);
        return R.success(comment);
    }

    @Override
    public R deleteComment(Integer id) {
        mapper.deleteById(id);
        return R.success();
    }

    @Override
    public R getPage(PageDTO pageDTO, Integer postId) {
        LambdaQueryWrapper<PostComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostComment::getPostId, postId);
        queryWrapper.gt(PostComment::getId, pageDTO.getLastId());
        queryWrapper.orderByDesc(PostComment::getId);
        Page<PostComment> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        page = mapper.selectPage(page, queryWrapper);
        PageVo vo = new PageVo();
        List<PostComment> records = page.getRecords();
        vo.setTotal((int) page.getTotal());
        vo.setPageNum(pageDTO.getPageNum());
        vo.setList(records);
        // 安全处理lastIndex
        if (!records.isEmpty()) {
            vo.setLastIndex(records.get(records.size() - 1).getId());
        } else {
            vo.setLastIndex(pageDTO.getLastId());
        }

        return R.success(vo);
    }
}
