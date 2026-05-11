package com.printtemplate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.printtemplate.module.*.mapper")
@EnableCaching
public class PrintTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrintTemplateApplication.class, args);
    }
}
