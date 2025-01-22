package com.aviobook.schedule.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev || prod")
public class OpenApiConfig {

    @Value("${api.url}")
    private String url;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server()
                .url(url)
                .description("Server URL in Development environment");

        Server prodServer = new Server()
                .url("https://schedule-api-0nlu.onrender.com")
                .description("Server URL in Production environment");

        Info info = new Info()
                .title("Flight scheduling API")
                .version("1.0")
                .summary("Schedule, view and cancel flights");

        return new OpenAPI()
                .servers(List.of(devServer, prodServer))
                .info(info);
    }
}
