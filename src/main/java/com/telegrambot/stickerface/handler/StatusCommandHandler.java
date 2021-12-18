package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.service.MirroringUrlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StatusCommandHandler extends AbstractHandler {

    private static final String STATUS_REPLY_MESSAGE = "Bot is now mirroring %s";

    @Autowired
    private MirroringUrlService urlService;

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        bot.execute(getDefaultMessage(chatId, message, STATUS_REPLY_MESSAGE, urlService.getUrl() == null ? "no community" : urlService.getUrl()));
    }
}
