package com.example.haiyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Chen
 * @since 2025-06-01
 */
@Getter
@Setter
@TableName("chat")
public class Chat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("userId")
    private Integer userId;

    /**
     * 0 是用户， 1是AI
     */
    @TableField("isAI")
    private Boolean isAI;

    /**
     * 对话内容
     */
    @TableField("msg")
    private String msg;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
