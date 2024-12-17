package com.example.haiyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Chen
 * @since 2024-12-16
 */
@Getter
@Setter
@TableName("article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 0 -> 普通  1 -> 区域  2 -> 海洋  3 -> 地球  4 -> 基地
     */
    @TableField("type")
    private Integer type;

    /**
     * 首页图片
     */
    @TableField("img_url")
    private String imgUrl;

    /**
     * 链接
     */
    @TableField("article_url")
    private String articleUrl;

    /**
     * 标题
     */
    @TableField("title")
    private String title;
}
