package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class DefaultCommandHandler extends AbstractHandler {

    private static final String DEFAULT_REPLY_MESSAGE = "Please choose actions from menu.\n" +
            "Or type \"/help\" for see all actions.";

    DefaultCommandHandler(Bot bot) {
        super(bot);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, DEFAULT_REPLY_MESSAGE, "", null)));
    }
}
