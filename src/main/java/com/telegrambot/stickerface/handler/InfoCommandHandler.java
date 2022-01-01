package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

public class InfoCommandHandler extends AbstractHandler implements BotHandler {

    private static final String INFO_REPLY_MESSAGE = "This bot is mirroring your VK community content to your telegram channel\n" +
            "Creator is @hubbarus. For donation do something";

    InfoCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, INFO_REPLY_MESSAGE, "", null)));
    }
}
