package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.HerokuConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Slf4j
public class RegisterCommandHandler extends AbstractHandler implements BotHandler {

    private static final String REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE = "User not logged in! Please call /login first";
    private static final String REGISTER_READY_REPLY_MESSAGE = "Please send an url. Or type \"/skip\" to abort URL registration";

    RegisterCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard, HerokuConfig herokuConfig) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard, herokuConfig);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException, ClientException, ApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        if (user.isLoggedIn()) {
            bot.setRegisterCommandCalled(true);
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_READY_REPLY_MESSAGE, "", null)));
        } else {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE, "", null)));
        }
    }
}