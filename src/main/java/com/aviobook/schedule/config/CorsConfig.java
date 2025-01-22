package com.aviobook.schedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // allow any localhost on any port to access the API
                .allowedOriginPatterns("http://localhost(:[0-9]+)?")
                .allowedOrigins("https://some-single-page-app.com")
                .allowedMethods("OPTIONS", "GET", "POST", "DELETE");
    }
}
