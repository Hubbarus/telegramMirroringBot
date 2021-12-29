package com.telegrambot.stickerface.listener;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class ChannelListener extends AbstractListener {

    ChannelListener(Update update) {
        super(update);
    }

    @Override
    Long getChatId() {
        return update.getChannelPost().getChatId();
    }

    @Override
    String getInputText() {
        return update.getChannelPost().getText();
    }

    @Override
    Message getMessage() {
        return update.getChannelPost();
    }
}
