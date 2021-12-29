package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class StopCommandHandler extends AbstractHandler {

    private static final String STOP_SUCCESS_REPLY_MESSAGE = "Bot have stopped to polling messages";
    private static final String STOP_FAIL_REPLY_MESSAGE = "Bot doing nothing right now";

    private final MirroringUrlService urlService;
    private final ScheduledExecutorService executorService;

    @Autowired
    public StopCommandHandler(MirroringUrlService urlService, Bot bot, ScheduledExecutorService executorService) {
        super(bot);
        this.urlService = urlService;
        this.executorService = executorService;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);
        if (user.isRegistered()) {
            user.setRegistered(false);
            user.setUrl(null);
            executorService.shutdown();
            urlService.saveBotUser(user);
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STOP_SUCCESS_REPLY_MESSAGE, "")));
        } else {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STOP_FAIL_REPLY_MESSAGE, "")));
        }
    }
}
