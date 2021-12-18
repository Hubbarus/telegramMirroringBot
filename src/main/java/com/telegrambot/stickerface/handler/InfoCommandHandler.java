package com.telegrambot.stickerface.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class InfoCommandHandler extends AbstractHandler {

    private static final String INFO_REPLY_MESSAGE = "This bot is mirroring your VK community content to your telegram channel\n" +
            "Creator is @hubbarus. For donation do something";

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        bot.execute(getDefaultMessage(chatId, message, INFO_REPLY_MESSAGE, ""));
    }
}
