package com.example.haiyang.service.impl;

import com.example.haiyang.entity.InternshipBase;
import com.example.haiyang.dao.InternshipBaseMapper;
import com.example.haiyang.service.IInternshipBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2025-2-17
 */
@Service
public class InternshipBaseServiceImpl extends ServiceImpl<InternshipBaseMapper, InternshipBase> implements IInternshipBaseService {
    @Autowired
    private InternshipBaseMapper mapper;

    @Override
    public R getAll() {
        return R.success(list());
    }
}
