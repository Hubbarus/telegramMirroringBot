package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Getter
@Setter
@Component
public abstract class AbstractHandler {

    @Autowired
    protected Bot bot;

    public abstract List<Message> handle(long chatId, Message message) throws TelegramApiException, InterruptedException;

    void deleteOwnMessage(long chatId, Message receivedMessage) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(receivedMessage.getMessageId());
        bot.execute(deleteMessage);
    }

    SendMessage getDefaultMessage(long chatId, String returnText, String extraText) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(String.valueOf(chatId));
        messageToSend.setText(String.format(returnText, extraText));
        return messageToSend;
    }

}
