package com.example.haiyang;

import cn.hutool.Hutool;
import cn.hutool.json.JSONUtil;
import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.entity.Log;
import com.example.haiyang.util.MyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

/**
 * @Author Cbc
 * @DateTime 2024/10/23 21:43
 * @Description
 */
public class BMTest {

    public static void main(String[] args) throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Log log = new Log();
        log.setCreateTime(now);
        String jsonStr = JSONUtil.toJsonStr(log);
        System.out.println(jsonStr);
    }
}
