package com.example.haiyang.service;

import com.example.haiyang.entity.InternshipBase;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.util.R;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
public interface IInternshipBaseService extends IService<InternshipBase> {

    R getAll();
}
