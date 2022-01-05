package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import com.telegrambot.stickerface.listener.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class PostingService implements Runnable {

    private final Long chatId;
    private final Bot bot;
    private final MirroringUrlService urlService;

    public PostingService(long chatId, Bot bot, MirroringUrlService urlService) {
        this.chatId = chatId;
        this.bot = bot;
        this.urlService = urlService;
    }

    @Override
    public void run() {
        String chatIdString = String.valueOf(chatId);
        ConcurrentLinkedQueue<VkMessage> messageQueue = urlService.getMessageQueue();
        log.info("Not posted messages count: " + messageQueue.size());
        VkMessage messageFromQueue = messageQueue.poll();

        if (messageFromQueue != null) {
            SendPhoto image = messageFromQueue.getImage();
            SendMediaGroup mediaGroup = messageFromQueue.getMediaGroup();
            SendMessage messageCaption = messageFromQueue.getMessageCaption();
            SendMessage notPhotoMessage = messageFromQueue.getNotPhotoMessage();

            try {
                log.info("Posting message from queue to chat...");

                if (image != null) {
                    image.setChatId(chatIdString);
                    bot.execute(image);
                    addCaptionIfExists(messageCaption, chatIdString);
                } else if (mediaGroup != null) {
                    mediaGroup.setChatId(chatIdString);
                    bot.execute(mediaGroup);
                    addCaptionIfExists(messageCaption, chatIdString);
                } else if (notPhotoMessage != null) {
                    notPhotoMessage.setChatId(chatIdString);
                    bot.execute(notPhotoMessage);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCaptionIfExists(SendMessage messageCaption, String chatIdString) throws TelegramApiException {
        if (messageCaption != null) {
            messageCaption.setChatId(chatIdString);
            bot.execute(messageCaption);
        }
    }
}
