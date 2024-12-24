package com.example.haiyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.haiyang.dto.LogPageDTO;
import com.example.haiyang.entity.Log;
import com.example.haiyang.dao.LogMapper;
import com.example.haiyang.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import com.example.haiyang.vo.LogPageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2024-10-31
 */
@Service
@Slf4j
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public R saveLog(Log log) {
        //完善
        log.setUserId(MyThreadLocal.getUserId());
        log.setCreateTime(LocalDateTime.now());
        //todo 点位等
        save(log);
        return R.successMsg("保存成功");
    }

    @Override
    public R getPage(LogPageDTO logPageDTO) {

        Page<Log> page = new Page<>(logPageDTO.getPageNum(), logPageDTO.getPageSize());

        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(logPageDTO.getStartTime() != null && logPageDTO.getEndTime() != null,
                Log::getCreateTime,
                logPageDTO.getStartTime(),
                logPageDTO.getEndTime());
        wrapper.orderByDesc(Log::getCreateTime);
        //userId
        wrapper.eq(Log::getUserId, MyThreadLocal.getUserId());
        Page<Log> pageResult = logMapper.selectPage(page, wrapper);


        LogPageVo vo = new LogPageVo();
        vo.setList(pageResult.getRecords());
        vo.setTotal((int) pageResult.getTotal());

        return R.success(vo);
    }

    @Override
    public R del(Integer id) {
        logMapper.deleteById(id);
        return R.successMsg("del成功");
    }

    @Override
    public R updateLog(Log l) {
        updateById(l);
        return R.success("update-success", l);
    }

}
