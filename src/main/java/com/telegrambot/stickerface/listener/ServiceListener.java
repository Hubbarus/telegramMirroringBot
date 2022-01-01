package com.telegrambot.stickerface.listener;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ServiceListener extends AbstractListener {

    ServiceListener(Update update) {
        super(update);
    }

    @Override
    Long getChatId() {
        return update.getMyChatMember().getChat().getId();
    }

    @Override
    String getInputText() {
        return null;
    }

    @Override
    Message getMessage() {
        return update.getMessage();
    }
}
