package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


//前后端链接
@Configuration
public class CorsFilterConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的前端域名（Vue 运行在 5173 端口）
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*"); // 允许所有 HTTP 方法（GET/POST 等）

//        config.addAllowedHeader("Token");
        config.addAllowedHeader("*");
         // 允许所有请求头（包括自定义头）
        config.setAllowCredentials(true); // 允许携带 Cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口生效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}