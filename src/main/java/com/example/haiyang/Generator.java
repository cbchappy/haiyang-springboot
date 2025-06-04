package com.example.haiyang;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * @Author Cbc
 * @DateTime 2024/10/13 13:46
 * @Description
 */
public class Generator {

    public static void main(String[] args) {
        //todo 端口改变
        FastAutoGenerator.create("jdbc:mysql://8.136.101.132:3306/haiyang?useUnicode=true&characterEncoding=utf-8", "haiyang", "haiyang-2025")
                .globalConfig(builder -> {
                    builder.author("Chen") // 设置作者
                            .outputDir("src\\main\\java"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.haiyang") // 设置父包名
                            .entity("entity") // 设置实体类包名
                            .mapper("dao") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mappers"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("chat") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}
