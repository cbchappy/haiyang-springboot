package com.example.haiyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Chen
 * @since 2024-10-31
 */
@Getter
@Setter
@TableName("log")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 创建日期 yyy-mm-dd hh:mm:ss
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField("user_id")
    private Integer userId;

    /**
     * 天气
     */
    @TableField("weather")
    private String weather;

    /**
     * 地点
     */
    @TableField("location")
    private String location;

    /**
     * 路线
     */
    @TableField("route")
    private String route;

    /**
     * 任务
     */
    @TableField("task")
    private String task;

    /**
     * 点号
     */
    @TableField("dot_mark")
    private String dotMark;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 高度
     */
    private Double altitude;


    /**
     * 点位描述
     */
    @TableField("description")
    private String description;
}
