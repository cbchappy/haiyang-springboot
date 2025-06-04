package com.example.haiyang.service;

import com.example.haiyang.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2025-06-01
 */
public interface IChatService extends IService<Chat> {
    /**
     * 根据id获取用户对话信息
     */
    List<Chat> getListByUserId(Integer userId);
}
