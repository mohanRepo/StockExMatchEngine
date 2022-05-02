package com.dse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
public class StockExOrderMatchEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockExOrderMatchEngineApplication.class, args);
    }
}
