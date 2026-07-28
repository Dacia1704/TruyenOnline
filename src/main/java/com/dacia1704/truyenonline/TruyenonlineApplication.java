package com.dacia1704.truyenonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TruyenonlineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TruyenonlineApplication.class, args);
    }
}
