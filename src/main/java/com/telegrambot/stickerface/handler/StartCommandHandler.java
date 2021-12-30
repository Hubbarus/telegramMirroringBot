package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class StartCommandHandler extends AbstractHandler {

    private static final String START_REPLY_MESSAGE = "Hello, %s! So let's start! Choose action from menu";

    StartCommandHandler(Bot bot) {
        super(bot);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        String userName;
        if (message.getChat().getUserName() != null) {
            userName = message.getChat().getUserName();
        } else if (message.getChat().getFirstName() != null) {
            userName = message.getChat().getFirstName() + message.getChat().getLastName();
        } else {
            userName = message.getChat().getTitle();
        }
        return Collections.singletonList(bot.execute(this.getDefaultMessage(chatId, START_REPLY_MESSAGE, userName)));
    }
}
