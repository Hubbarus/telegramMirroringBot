package com.telegrambot.stickerface.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class DefaultCommandHandler extends AbstractHandler {

    private static final String DEFAULT_REPLY_MESSAGE = "Please choose actions from menu.\n" +
            "Or type \"/help\" for see all actions.";

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        bot.execute(getDefaultMessage(chatId, message, DEFAULT_REPLY_MESSAGE, ""));
    }
}
