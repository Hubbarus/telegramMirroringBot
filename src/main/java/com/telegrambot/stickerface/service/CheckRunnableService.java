package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.model.BotUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class CheckRunnableService implements Runnable {

    private Long chatId;
    private ScheduledExecutorService executorService;
    private MirroringUrlService urlService;

    public CheckRunnableService(Long chatId, ScheduledExecutorService executorService, MirroringUrlService urlService) {
        this.chatId = chatId;
        this.executorService = executorService;
        this.urlService = urlService;
    }

    @SneakyThrows
    @Override
    public void run() {
        BotUser user = urlService.getBotUserByChatId(chatId);
        if (user != null) {
            if (user.isRegistered()) {
                log.info("User didn't stop execution yet. Continue polling...");
                Thread.sleep(10000);
            } else {
                log.info("User have called /stop command. Shutting down threads!");
                executorService.shutdown();
            }
        }
    }
}
