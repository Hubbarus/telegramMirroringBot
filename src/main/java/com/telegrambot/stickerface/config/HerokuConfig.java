package com.telegrambot.stickerface.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "heroku")
@Getter
@Setter
@Deprecated
public class HerokuConfig {
    private String lifecheckUrl;
    private int lifecheckPeriod;
    private int lifecheckDelay;
}
