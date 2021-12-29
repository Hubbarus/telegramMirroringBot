package com.telegrambot.stickerface.listener;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class PersonalChatListener extends AbstractListener {

    PersonalChatListener(Update update) {
        super(update);
    }

    @Override
    Long getChatId() {
        return update.getMessage().getChatId();
    }

    @Override
    String getInputText() {
        return update.getMessage().getText();
    }

    @Override
    Message getMessage() {
        return update.getMessage();
    }
}
