package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Slf4j
public class ServiceHandler extends AbstractHandler {
    ServiceHandler(Bot bot) {
        super(bot);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws Exception {
        log.info("Service message received...");
        return null;
    }
}
