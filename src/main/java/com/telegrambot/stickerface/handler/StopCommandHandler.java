package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

public class StopCommandHandler extends AbstractHandler implements BotHandler {

    private static final String STOP_SUCCESS_REPLY_MESSAGE = "Bot have stopped to polling messages";
    private static final String STOP_FAIL_REPLY_MESSAGE = "Bot doing nothing right now";

    StopCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);
        synchronized (urlService) {
            if (!user.isStopped()) {
                user.setStopped(true);
                urlService.notifyAll();
                urlService.saveBotUser(user);
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STOP_SUCCESS_REPLY_MESSAGE, "", null)));
            } else {
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STOP_FAIL_REPLY_MESSAGE, "", null)));
            }
        }
    }
}
