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
@TableName("internship_base")
public class InternshipBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("address")
    private String address;

    @TableField("imgUrl")
    private String imgUrl;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;
}
