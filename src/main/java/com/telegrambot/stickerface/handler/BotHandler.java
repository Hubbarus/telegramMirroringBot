package com.telegrambot.stickerface.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public interface BotHandler {

    List<Message> handle(long chatId, Message message) throws Exception;

    default SendMessage getDefaultMessage(long chatId, String returnText, String extraText, ReplyKeyboardMarkup keyboard) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setReplyMarkup(keyboard);
        messageToSend.setChatId(String.valueOf(chatId));
        messageToSend.setText(String.format(returnText, extraText));
        return messageToSend;
    }
}
