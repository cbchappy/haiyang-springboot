package com.example.haiyang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan("com.example.haiyang.dao")
public class HaiyangApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaiyangApplication.class, args);
    }
}
