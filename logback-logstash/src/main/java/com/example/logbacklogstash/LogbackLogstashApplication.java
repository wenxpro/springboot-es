package com.example.logbacklogstash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class LogbackLogstashApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogbackLogstashApplication.class, args);
    }

}
