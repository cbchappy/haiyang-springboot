package com.example.haiyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.haiyang.entity.Chat;
import com.example.haiyang.dao.ChatMapper;
import com.example.haiyang.service.IChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2025-2-15
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements IChatService {


    @Override
    public List<Chat> getListByUserId(Integer userId) {
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getUserId, userId);
        queryWrapper.orderByDesc(Chat::getId);
        List<Chat> list = list(queryWrapper);
        if(list.size() > 1000){
            List<Integer> delList = list.stream().skip(1000).map(Chat::getId).toList();
            //noinspection SpringTransactionalMethodCallsInspection
            removeBatchByIds(delList);

        }
        return list;
    }
}
