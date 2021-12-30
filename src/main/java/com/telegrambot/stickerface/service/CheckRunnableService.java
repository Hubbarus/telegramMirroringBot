package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.model.BotUser;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class CheckRunnableService implements Runnable {

    private final Long chatId;
    private final ScheduledExecutorService executorService;
    private final MirroringUrlService urlService;

    public CheckRunnableService(Long chatId, ScheduledExecutorService executorService, MirroringUrlService urlService) {
        this.chatId = chatId;
        this.executorService = executorService;
        this.urlService = urlService;
    }

    @Override
    public void run() {
        boolean isNotified = false;
        while (!isNotified) {
            synchronized (urlService) {
                BotUser user = urlService.getBotUserByChatId(chatId);
                if (user != null) {
                    if (!user.isStopped()) {
                        log.info("User didn't stop execution yet. Continue polling...");
                        try {
                            urlService.wait(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        log.info("User have called /stop command. Shutting down threads!");
                        executorService.shutdown();
                        isNotified = true;
                        urlService.saveBotUser(user);
                    }
                }
            }
        }
    }
}
