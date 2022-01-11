package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.LogsService;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Collections;
import java.util.List;

public class LogsCommandHandler extends AbstractHandler implements BotHandler {

    public LogsCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient,
                              Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard, LogsService logsService) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard, logsService);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws Exception {
        if (!bot.isLoggerCommandCalled()) {
            bot.setLoggerCommandCalled(true);
        }
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, "Enter number of hours to see logs...", "", keyboard)));
    }
}
