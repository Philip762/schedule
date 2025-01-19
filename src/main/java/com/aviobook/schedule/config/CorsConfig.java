package com.aviobook.schedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // allow dev environment to access the API
                .allowedOrigins("http://localhost:5000", "https://some-single-page-app.com")
                .allowedMethods("OPTIONS", "GET", "POST", "DELETE");
    }
}
