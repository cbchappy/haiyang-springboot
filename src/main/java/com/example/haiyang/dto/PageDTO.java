package com.example.haiyang.dto;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2025/6/8 13:31
 * @Description
 */
@Data
public class PageDTO {
    private Integer lastId;
    private Integer pageNum;
    private Integer pageSize;
}
