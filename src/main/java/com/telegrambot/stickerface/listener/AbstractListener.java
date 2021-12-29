package com.telegrambot.stickerface.listener;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

abstract class AbstractListener {

    Update update;

    AbstractListener(Update update) {
        this.update = update;
    }

    abstract Long getChatId();

    abstract String getInputText();

    abstract Message getMessage();
}
