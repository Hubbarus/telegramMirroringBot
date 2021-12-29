package com.telegrambot.stickerface.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ConfigurationProperties(prefix = "bot")
@Configuration
@Getter
@Setter
public class BotConfig {

    private String name;

    private String token;

    @Bean
    public ScheduledExecutorService getscheduledExecutorService() {
        return Executors.newScheduledThreadPool(3);
    }
}