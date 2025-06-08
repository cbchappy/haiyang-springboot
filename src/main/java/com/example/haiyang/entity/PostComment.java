package com.example.haiyang.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2025-2-17
 */
@Getter
@Setter
@TableName("post_comment")
public class PostComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("post_id")
    private Integer postId;

    @TableField("content")
    private String content;

    @TableField("create_time")
    private LocalDateTime createTime;
}
