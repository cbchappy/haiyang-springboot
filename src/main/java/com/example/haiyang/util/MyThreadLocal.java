package com.example.haiyang.util;

/**
 * @Author Cbc
 * @DateTime 2024/10/17 15:49
 * @Description
 */
public class MyThreadLocal {
    private final static ThreadLocal<Integer> IdThreadLocal = new ThreadLocal<>();

    public static Integer getUserId(){
        return IdThreadLocal.get();
    }

    public static void setUserId(Integer id){
        IdThreadLocal.set(id);
    }

    public static void removeUserId(){
        IdThreadLocal.remove();
    }



}
