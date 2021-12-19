package com.telegrambot.stickerface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.starter.AfterBotRegistration;

@SpringBootApplication
@EnableConfigurationProperties
public class StickerfaceApplication {

	@AfterBotRegistration
	public static void main(String[] args) {
		SpringApplication.run(StickerfaceApplication.class, args);
	}

}
