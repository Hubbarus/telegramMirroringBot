package com.telegrambot.stickerface.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
public class CheckRunnableService implements Runnable {

    @Autowired
    private ScheduledExecutorService executorService;

    @Autowired
    private
    MirroringUrlService urlService;

    @SneakyThrows
    @Override
    public void run() {
        while (urlService.isRegistered()) {
            log.info("User didn't stop execution yet. Continue polling...");
            Thread.sleep(10000);
        }
        log.info("User have called /stop command. Shutting down threads!");
        executorService.shutdown();
    }
}
