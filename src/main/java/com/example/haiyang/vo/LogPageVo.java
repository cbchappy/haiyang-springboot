package com.example.haiyang.vo;

import com.example.haiyang.entity.Log;
import lombok.Data;

import java.util.List;

/**
 * @Author Cbc
 * @DateTime 2024/11/4 20:11
 * @Description
 */
@Data
public class LogPageVo {

    private Integer total;

    private List<Log> list;
}
