package com.example.haiyang;

import com.alibaba.fastjson.JSON;
import com.example.haiyang.entity.User;
import com.example.haiyang.util.BigModel;
import com.example.haiyang.util.MyUtil;

public class Rethrowing {
    public static void f() throws Exception {

        BigModel.RoleContent roleContent = new BigModel.RoleContent();
        roleContent.setContent("odoodoq");
        roleContent.setRole("user");
        String s = JSON.toJSONString(roleContent);
        System.out.println(s);
        System.out.println(JSON.toJSON(roleContent));
    }

    public static void g() throws Exception {
        f();
    }

    public static void main(String[] args) {
        try {
            g();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //id userId url params method opeMethod exceptionName exceptionContent createTime
        /*
         * Inside g(), e.printStackTrace():
         * java.lang.Exception: Thrown from f()
         *     at exception.Rethrowing.f(Rethrowing.java:6)
         *     at exception.Rethrowing.g(Rethrowing.java:11)
         *     at exception.Rethrowing.main(Rethrowing.java:21)
         * Inside main(), e.printStackTrace():
         * java.lang.Exception: Thrown from f()
         *     at exception.Rethrowing.g(Rethrowing.java:15)
         *     at exception.Rethrowing.main(Rethrowing.java:21)
         */
    }
}