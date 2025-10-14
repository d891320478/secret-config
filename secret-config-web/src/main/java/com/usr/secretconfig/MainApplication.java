package com.usr.secretconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dht31261
 * @date 2025年10月12日 20:11:47
 */
@SpringBootApplication(scanBasePackages = { "com.usr" })
@EnableScheduling
@Slf4j
public class MainApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(MainApplication.class, args);
        } catch (Throwable e) {
            log.error("main.", e);
        }
    }
}