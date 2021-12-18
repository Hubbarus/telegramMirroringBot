package com.telegrambot.stickerface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.starter.AfterBotRegistration;
import org.telegram.telegrambots.starter.TelegramBotInitializer;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootApplication
@EnableConfigurationProperties
public class StickerfaceApplication {

	@AfterBotRegistration
	public static void main(String[] args) {
		SpringApplication.run(StickerfaceApplication.class, args);
	}

}
