package com.aviobook.schedule.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${api.url}")
    private String url;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(url);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl("https://schedule-api-0nlu.onrender.com");
        prodServer.setDescription("Server URL in Production environment");

        Info info = new Info()
                .title("Flight scheduling API")
                .version("1.0")
                .summary("This API is used to schedule and manage airline flights.");

        return new OpenAPI()
                .servers(List.of(devServer, prodServer))
                .info(info);
    }
}
