package com.telegrambot.stickerface.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartCommandHandler extends AbstractHandler {

    private static final String START_REPLY_MESSAGE = "Hello, %s! So let's start! Choose action from menu";

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        String userName = message.getChat().getUserName() != null ? message.getChat().getUserName()
                : message.getChat().getFirstName() + message.getChat().getLastName();
        bot.execute(this.getDefaultMessage(chatId, message, START_REPLY_MESSAGE, userName));
    }
}
