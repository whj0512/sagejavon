package com.springboot.cli.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> filterRegistrationBean() {
        // 创建 CORS 配置
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1. 允许所有来源
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));

        // 2. 允许所有请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);

        // 3. 允许所有方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        // 4. 允许凭证
        corsConfiguration.setAllowCredentials(true);

        // 注册 CORS 配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        // 创建 CORS 过滤器
        CorsFilter corsFilter = new CorsFilter((CorsConfigurationSource) source);

        // 注册过滤器
        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(corsFilter);
        filterRegistrationBean.setOrder(-101);  // 设置优先级

        return filterRegistrationBean;
    }
}
