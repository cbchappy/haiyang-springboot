package com.example.haiyang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.haiyang.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Chen
 * @since 2024-12-16
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
