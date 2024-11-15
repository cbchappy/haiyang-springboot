package com.example.haiyang.service;

import com.example.haiyang.entity.Advice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.util.R;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2024-10-31
 */
public interface IAdviceService extends IService<Advice> {

    R saveAdvice(Advice advice);
}
