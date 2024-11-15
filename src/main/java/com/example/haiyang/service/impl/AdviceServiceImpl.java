package com.example.haiyang.service.impl;

import com.example.haiyang.entity.Advice;
import com.example.haiyang.dao.AdviceMapper;
import com.example.haiyang.service.IAdviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2024-10-31
 */
@Service
public class AdviceServiceImpl extends ServiceImpl<AdviceMapper, Advice> implements IAdviceService {

    @Override
    public R saveAdvice(Advice advice) {
        advice.setCreateTime(LocalDateTime.now());
        advice.setUserId(MyThreadLocal.getUserId());
        save(advice);
        return R.success("反馈成功!", null);
    }
}
