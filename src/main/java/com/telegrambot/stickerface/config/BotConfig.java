package com.telegrambot.stickerface.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@ConfigurationProperties(prefix = "bot")
@Configuration
@Getter
@Setter
public class BotConfig {

    private String name;
    private String token;
    private int initialPollingDelay;
    private int initialPostingDelay;
    private int pollingPeriod;
    private int postingPeriod;

    @Bean
    public ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setInputFieldPlaceholder("Select a command...");
        return replyKeyboardMarkup;
    }
}