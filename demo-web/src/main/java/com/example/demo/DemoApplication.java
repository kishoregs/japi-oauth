package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = {"com.example.demo", "com.example.demo.service", "com.example.demo.controller", "com.example.demo.config"})
public class DemoApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        logger.info("Configuring SpringApplicationBuilder");
        return application.sources(DemoApplication.class);
    }

    public static void main(String[] args) {
        logger.info("Starting DemoApplication");
        SpringApplication.run(DemoApplication.class, args);
        logger.info("DemoApplication started successfully");
    }
}