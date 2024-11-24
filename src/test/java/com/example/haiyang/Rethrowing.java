package com.example.haiyang;

import com.example.haiyang.util.MyUtil;

public class Rethrowing {
    public static void f() throws Exception {

        Exception exception = new Exception("Thrown from f()");
        Throwable c1 = new Throwable("这是cause1");
        c1.initCause(new Throwable("这是cause2"));
        exception.initCause(c1);
        throw exception;
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