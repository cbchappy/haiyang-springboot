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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @Author Cbc
 * @DateTime 2024/10/23 21:43
 * @Description
 */
public class BMTest {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Log> map = new HashMap<>();
        Log log = new Log();
        log.setUserId(1);

        map.put("1", log);
        System.out.println(JSONUtil.toJsonStr(map));
    }
}
