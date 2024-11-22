package com.gallery_app.core_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class CoreServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CoreServiceApplication.class, args);
    }

}
