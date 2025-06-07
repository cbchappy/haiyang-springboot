package com.example.haiyang.vo;

import com.example.haiyang.entity.Article;
import lombok.Data;

import java.util.List;

/**
 * @Author Cbc
 * @DateTime 2025/1/7 16:42
 * @Description
 */
@Data
public class ArticlePageVO {
    private Integer lastIndex;
    private List<Article> articleList;
    private Integer total;
    private Integer pageNum;
}
