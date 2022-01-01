package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatusCommandHandler extends AbstractHandler implements BotHandler {

    private static final String STATUS_REPLY_MESSAGE = "Bot is now mirroring %s";

    StatusCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        String communityString = user.getVkCommunities().isEmpty() ? "nothing" : user.getVkCommunities().stream()
                .map(VkCommunity::getName)
                .map(name -> "'".concat(name).concat("'"))
                .collect(Collectors.joining(", "));

        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STATUS_REPLY_MESSAGE, communityString, null)));
    }
}
