package com.springboot.cli.common.config;

import com.springboot.cli.common.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 配置拦截器路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AppProperties appProperties;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(appProperties))
                // 拦截的路径
                .addPathPatterns("/**")
                // 开放的路径
                .excludePathPatterns("/login/**", "/token/validate", "/student/register","/student/login", "/login", "/register","register","/python/login/**", "/python/token/validate", "/python/student/register","/python/student/login", "/python/login", "/python/register","/python/register"
                ,"/backend/**");
    }

    /**
     * 全局跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3003","http://localhost:8000","http://localhost:8080" ,"http://117.72.59.61", "http://127.0.0.1:3003")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
