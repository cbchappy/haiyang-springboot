package com.example.haiyang.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Cbc
 * @DateTime 2024/11/4 19:59
 * @Description
 */
@Data
public class LogPageDTO {
    private Integer pageNum;

    private Integer pageSize;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
