package com.aviobook.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }

    // http://localhost:8080/swagger-ui/index.html
    // TODO: validaition on request parameter (what if empty, what if not correct type)
}
