package com.telegrambot.stickerface.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ConfigurationProperties(prefix = "bot")
@Component
@Getter
@Setter
public class BotConfig {

    @Value("username")
    private String username;

    @Value("token")
    private String token;

    @Bean
    public ScheduledExecutorService getscheduledExecutorService() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        return scheduledExecutorService;
    }
}
