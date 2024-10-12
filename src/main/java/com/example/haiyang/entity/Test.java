package com.example.haiyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/10/11 12:23
 * @Description
 */
@TableName("test")
@Data
public class Test {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String info;
}
