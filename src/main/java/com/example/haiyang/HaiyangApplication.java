package com.example.haiyang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.haiyang.mapper")
public class HaiyangApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaiyangApplication.class, args);
    }

}
