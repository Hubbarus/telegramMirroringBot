package com.telegrambot.stickerface.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.telegrambot.stickerface.repository"})
@EntityScan(basePackages = {"com.telegrambot.stickerface.model"})
public class JpaConfig {
}
