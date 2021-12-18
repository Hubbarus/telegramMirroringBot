package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import com.telegrambot.stickerface.listener.Bot;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Setter
@Slf4j
public class PostingService implements Runnable {

    private long chatId;

    @Autowired
    private Bot bot;

    @Autowired
    private MirroringUrlService urlService;

    @Override
    public void run() {
        while (!urlService.getMessageQueue().isEmpty()) {
            VkMessage messageFromQueue = urlService.getMessageQueue().poll();
            messageFromQueue.getImage().setChatId(String.valueOf(chatId));

            try {
                if (messageFromQueue.getText() != null) {
                    SendMessage message = new SendMessage();
                    message.setText(messageFromQueue.getText());
                    message.setChatId(String.valueOf(chatId));
                    bot.execute(message);
                }

                log.info("Posting message from queue to chat...");
                bot.execute(messageFromQueue.getImage());
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }
}
