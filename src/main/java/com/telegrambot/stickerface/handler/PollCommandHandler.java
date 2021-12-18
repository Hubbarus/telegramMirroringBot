package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.service.CheckRunnableService;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.service.PollingService;
import com.telegrambot.stickerface.service.PostingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PollCommandHandler extends AbstractHandler {

    private static final String POLL_FAIL_REPLY_MESSAGE = "No url specified. Run /register command first!";

    @Autowired
    private MirroringUrlService urlService;

    @Autowired
    private PollingService pollingService;

    @Autowired
    private PostingService postingService;

    @Autowired
    private CheckRunnableService checkService;

    @Autowired
    private ScheduledExecutorService executorService;

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException, InterruptedException {
        if (urlService.getUrl() == null) {
            bot.execute(getDefaultMessage(chatId, message, POLL_FAIL_REPLY_MESSAGE, ""));
            return;
        }

        log.info("Starting polling, posting and checking threads...");
        urlService.setRegistered(true);
        postingService.setChatId(chatId);
        executorService.scheduleAtFixedRate(pollingService, 0, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(postingService, 5, 10, TimeUnit.SECONDS);
        executorService.schedule(checkService, 5, TimeUnit.SECONDS);
    }
}
