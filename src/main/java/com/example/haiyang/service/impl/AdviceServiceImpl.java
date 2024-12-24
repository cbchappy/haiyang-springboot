package com.example.haiyang.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.util.StrUtil;
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
        if(StrUtil.isBlankIfStr(advice.getContent())){
            return R.failMsg("反馈内容不能为空!");
        }
        advice.setCreateTime(LocalDateTime.now());
        advice.setUserId(MyThreadLocal.getUserId());
        save(advice);
        return R.successMsg("反馈成功!");
    }
}
