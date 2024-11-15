package com.example.haiyang.service;

import com.example.haiyang.dto.LogPageDTO;
import com.example.haiyang.entity.Log;
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
public interface ILogService extends IService<Log> {

    R saveLog(Log log);

    R getPage(LogPageDTO logPageDTO);

    R del(Integer id);

    R updateLog(Log l);
}
