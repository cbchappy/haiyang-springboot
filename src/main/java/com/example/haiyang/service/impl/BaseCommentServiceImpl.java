package com.example.haiyang.service.impl;

import cn.hutool.Hutool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.haiyang.dto.PageDTO;
import com.example.haiyang.entity.BaseComment;
import com.example.haiyang.dao.BaseCommentMapper;
import com.example.haiyang.service.IBaseCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.R;
import com.example.haiyang.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
public class BaseCommentServiceImpl extends ServiceImpl<BaseCommentMapper, BaseComment> implements IBaseCommentService {
    @Autowired
    private BaseCommentMapper mapper;

    @Override
    public R addComment(BaseComment comment) {
        String content = comment.getContent();
        if(content == null || content.equals("")){
            return R.failMsg("评论不能为空!");
        }
        comment.setCreateTime(LocalDateTime.now());
        mapper.insert(comment);
        return R.success();
    }

    @Override
    public R deleteComment(Integer id) {
        if(id == null){
            return R.failMsg("id不能为空!");
        }
        mapper.deleteById(id);
        return R.success();
    }

    @Override
    public R getPage(PageDTO pageDTO, Integer baseId) {
        LambdaQueryWrapper<BaseComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseComment::getBaseId, baseId);
        queryWrapper.gt(BaseComment::getBaseId, pageDTO.getLastId());
        queryWrapper.orderByDesc(BaseComment::getBaseId);
        Page<BaseComment> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        page = mapper.selectPage(page, queryWrapper);
        PageVo vo = new PageVo();
        List<BaseComment> records = page.getRecords();
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
