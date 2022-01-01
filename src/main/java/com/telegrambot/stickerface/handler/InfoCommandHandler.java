package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class InfoCommandHandler extends AbstractHandler {

    private static final String INFO_REPLY_MESSAGE = "This bot is mirroring your VK community content to your telegram channel\n" +
            "Creator is @hubbarus. For donation do something";

    InfoCommandHandler(Bot bot) {
        super(bot);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, INFO_REPLY_MESSAGE, "", null)));
    }
}
