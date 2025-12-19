package com.acpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync // Enable async email sending
public class AcpulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcpulseApplication.class, args);
    }
}
