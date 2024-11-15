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
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("operate_log")
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户
     */
    @TableField("userId")
    private Integer userId;

    @TableField("operate_time")
    private LocalDateTime operateTime;

    @TableField("operate_class")
    private String operateClass;

    @TableField("operate_method")
    private String operateMethod;

    @TableField("operate_parameters")
    private String operateParameters;

    @TableField("consuming_time")
    private Long consumingTime;//单位: ms

    /**
     * 返回结果
     */
    @TableField("res")
    private String res;
}
