package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class StatusCommandHandler extends AbstractHandler {

    private static final String STATUS_REPLY_MESSAGE = "Bot is now mirroring %s";

    @Autowired
    private MirroringUrlService urlService;

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STATUS_REPLY_MESSAGE,
                user.getUrl() == null ? "no community" : user.getUrl())));
    }
}
