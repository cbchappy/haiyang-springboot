package com.example.haiyang.vo;

import com.example.haiyang.entity.Article;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @Author Cbc
 * @DateTime 2025/2/8 13:50
 * @Description
 */
@Data
public class PageVo {
    private Integer lastIndex;
    private List<?> list;
    private Integer total;
    private Integer pageNum;
}
