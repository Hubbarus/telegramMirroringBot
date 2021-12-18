package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@Setter
@Component
public abstract class AbstractHandler {

    @Autowired
    protected Bot bot;

    public abstract void handle(long chatId, Message message) throws TelegramApiException, InterruptedException;

    SendMessage getDefaultMessage(long chatId, Message message, String returnText, String extraText) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(String.valueOf(chatId));
        messageToSend.setText(String.format(returnText, extraText));
        return messageToSend;
    };
}
