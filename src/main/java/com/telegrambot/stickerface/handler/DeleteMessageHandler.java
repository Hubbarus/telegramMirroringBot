package com.telegrambot.stickerface.handler;


import com.telegrambot.stickerface.listener.Bot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class DeleteMessageHandler implements Runnable {

    private Bot bot;
    private List<Message> ownMessages;

    public DeleteMessageHandler(Bot bot, List<Message> ownMessages) {
        this.bot = bot;
        this.ownMessages = ownMessages;
    }

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(30000);
        log.info("Deleting own messages from channel...");
        ownMessages.stream().filter(msg -> msg.getChatId() < 0)
                .forEach(msg -> {
                    DeleteMessage message = new DeleteMessage();
                    message.setChatId(String.valueOf(msg.getChatId()));
                    message.setMessageId(msg.getMessageId());
                    try {
                        bot.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
    }
}