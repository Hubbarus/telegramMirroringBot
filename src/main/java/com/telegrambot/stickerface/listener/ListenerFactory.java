package com.telegrambot.stickerface.listener;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class ListenerFactory {

    AbstractListener createListener(Update update) {
        if (update.getMessage() != null) {
            return new PersonalChatListener(update);
        } else if (update.getChannelPost() != null) {
            return new ChannelListener(update);
        } else {
            return new ServiceListener(update);
        }
    }
}
