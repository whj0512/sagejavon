package com.springboot.cli;

import com.springboot.cli.common.AppProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@MapperScan("com.springboot.cli.mapper")
@EnableAsync(proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
public class SageJavonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SageJavonApplication.class, args);
    }

}
