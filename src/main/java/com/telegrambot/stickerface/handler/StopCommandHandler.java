package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.service.MirroringUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StopCommandHandler extends AbstractHandler {

    private static final String STOP_SUCCESS_REPLY_MESSAGE = "Bot have stopped to polling messages";
    private static final String STOP_FAIL_REPLY_MESSAGE = "Bot doing nothing right now";

    @Autowired
    private MirroringUrlService urlService;

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        if (urlService.isRegistered()) {
            urlService.setRegistered(false);
            bot.execute(getDefaultMessage(chatId, message, STOP_SUCCESS_REPLY_MESSAGE, ""));
        } else {
            bot.execute(getDefaultMessage(chatId, message, STOP_FAIL_REPLY_MESSAGE, ""));
        }
    }
}
