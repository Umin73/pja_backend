package com.project.PJA.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins("http://localhost:5173") // 프론트 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // preflight 허용
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
