package com.example.haiyang.util;

/**
 * @Author Cbc
 * @DateTime 2024/10/17 15:49
 * @Description
 */
public class MyThreadLocal {
    private final static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static Integer getUserId(){
        return threadLocal.get();
    }

    public static void setUserId(Integer id){
        threadLocal.set(id);
    }
}
