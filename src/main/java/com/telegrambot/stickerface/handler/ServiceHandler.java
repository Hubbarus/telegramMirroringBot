package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.HerokuConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ServiceHandler extends AbstractHandler implements BotHandler {
    ServiceHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard, HerokuConfig herokuConfig) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard, herokuConfig);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws Exception {
        log.info("Service message received...");
        return Collections.emptyList();
    }
}
