package com.aviobook.schedule.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl("https://bezkoder-api.com");
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("philip12m2003@gmail.com");
        contact.setName("Philip Nous");

        Info info = new Info()
                .title("Flight scheduling API")
                .version("1.0")
                //.contact(contact)
                .summary("This API is used to schedule and manage airline flights.");

        return new OpenAPI().info(info);//.servers(List.of(devServer, prodServer));
    }
}
