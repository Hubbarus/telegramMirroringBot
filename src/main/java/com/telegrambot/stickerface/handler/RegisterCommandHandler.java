package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class RegisterCommandHandler extends AbstractHandler {

    private static final String REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE = "User not logged in! Please call /login first";
    private static final String REGISTER_READY_REPLY_MESSAGE = "Please send an url. Or type \"/skip\" to abort URL registration";

    private final MirroringUrlService urlService;

    RegisterCommandHandler(Bot bot, MirroringUrlService urlService) {
        super(bot);
        this.urlService = urlService;
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