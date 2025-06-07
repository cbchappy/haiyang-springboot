package com.example.haiyang.dto;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2025/1/7 16:36
 * @Description
 */
@Data
public class ArticlePageDTO {
    private Integer lastId;
    private Integer pageNum;
    private Integer pageSize;
}
