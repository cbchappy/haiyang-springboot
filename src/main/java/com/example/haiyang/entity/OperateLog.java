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
 * @since 2025-01-05
 */
@Getter
@Setter
@TableName("operate_log")
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * http请求method
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求路径
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求参数
     */
    @TableField("request_params")
    private String requestParams;

    /**
     * 请求体
     */
    @TableField("request_body")
    private String requestBody;

    /**
     * 操作的类
     */
    @TableField("class_name")
    private String className;

    /**
     * 操作的方法
     */
    @TableField("method_name")
    private String methodName;

    /**
     * 操作的方法的参数类型
     */
    @TableField("param_type")
    private String paramType;

    /**
     * 操作耗时, 单位为ms
     */
    @TableField("consume_time")
    private Integer consumeTime;

    /**
     * 操作结果 0->fail、  1->sucess、 2->exception 
     */
    @TableField("res_type")
    private Integer resType;

    /**
     * 结果, 如果报错则存储的是报错信息
     */
    @TableField("result")
    private String result;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    public static class ResTypeCode{
        public static int fail = 0;
        public static int success = 1;
        public static int exception = 2;
    }
}
