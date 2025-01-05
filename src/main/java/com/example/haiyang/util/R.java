package com.example.haiyang.util;

import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/10/13 14:04
 * @Description 结果类
 */
@Data
public class R {

    private Integer code;//0 失败,  1 成功, -1 错误

    private String msg;

    private Object data;

    public R(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R success(String msg, Object data){
        return new R(Code.success, msg, data);
    }

    public static R fail(String msg, Object data){
        return new R(Code.fail, msg, data);
    }

    public static R success(Object data){
        return new R(Code.success, "请求成功", data);
    }


    public static R failMsg(String msg){
        return new R(Code.fail, msg, null);
    }

    public static R success(){
        return new R(Code.success, "请求成功", null);
    }

    public static R successMsg(String msg){
        return new R(Code.success, msg, null);
    }

    public static  class Code{
        public static int fail = 0;
        public static int success = 1;
    }


}
