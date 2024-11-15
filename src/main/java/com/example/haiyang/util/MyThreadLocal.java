package com.example.haiyang.util;

/**
 * @Author Cbc
 * @DateTime 2024/10/17 15:49
 * @Description
 */
public class MyThreadLocal {
    private final static ThreadLocal<Integer> IdThreadLocal = new ThreadLocal<>();
    private final static ThreadLocal<String> ToeknThreadLocal = new ThreadLocal<>();

    public static Integer getUserId(){
        return IdThreadLocal.get();
    }

    public static void setUserId(Integer id){
        IdThreadLocal.set(id);
    }

    public static String getToken(){
        return ToeknThreadLocal.get();
    }

    public static void setToken(String token){
        ToeknThreadLocal.set(token);
    }


}
