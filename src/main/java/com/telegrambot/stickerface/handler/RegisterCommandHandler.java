package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class RegisterCommandHandler extends AbstractHandler {

    private static final String REGISTER_FAIL_REPLY_MESSAGE = "No url specified in input or more than one url provided. Check input!";
    private static final String REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE = "Invalid url: %s";
    private static final String REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE = "User not logged in! Please call /login first";
    private static final String REGISTER_SUCCESS_REPLY_MESSAGE = "Successfully registered community: %s";

    private final MirroringUrlService urlService;

    RegisterCommandHandler(Bot bot, MirroringUrlService urlService) {
        super(bot);
        this.urlService = urlService;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        if (user.isLoggedIn()) {
            String[] input = message.getText().split(" ");

            if (input.length != 2) {
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_REPLY_MESSAGE, "")));
            } else {
                if (urlService.isUrlValid(input[1])) {
                    user.setUrl(input[1]);
                    user.setRegistered(true);
                    urlService.saveBotUser(user);

                    return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_SUCCESS_REPLY_MESSAGE, user.getUrl())));
                } else {
                    return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE, input[1])));
                }
            }
        } else {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE, "")));
        }
    }
}