package com.example.haiyang;

import cn.hutool.Hutool;
import cn.hutool.json.JSONUtil;
import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.entity.Log;
import com.example.haiyang.util.BigModel;
import com.example.haiyang.util.MyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Field;
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
    public static boolean flag = true;
    public static A a = new A();
    public static void main(String[] args) throws Exception {
    }
}

class A{
    public volatile int t = 0;
}


