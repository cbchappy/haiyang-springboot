package com.example.haiyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2024-11-20
 */
@Getter
@Setter
@TableName("exception_log")
public class ExceptionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户的id，可为空
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("exception_name")
    private String exceptionName;

    @TableField("exception_content")
    private String exceptionContent;

    /**
     * 请求参数
     */
    @TableField("params")
    private String params;

    /**
     * 请求路径
     */
    @TableField("url")
    private String url;

    /**
     * 请求方式
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求体
     */
    @TableField("request_body")
    private String requestBody;

    /**
     * 操作的方法
     */
    @TableField("operate_method")
    private String operateMethod;
}
