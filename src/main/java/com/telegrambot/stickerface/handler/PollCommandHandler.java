package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.CheckRunnableService;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.service.PollingService;
import com.telegrambot.stickerface.service.PostingService;
import com.vk.api.sdk.client.VkApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PollCommandHandler extends AbstractHandler {

    private static final String POLL_FAIL_REPLY_MESSAGE = "No url specified. Run /register command first!";

    private final MirroringUrlService urlService;
    private final VkApiClient vkClient;
    private final ScheduledExecutorService executorService;

    @Autowired
    public PollCommandHandler(MirroringUrlService urlService, VkApiClient vkClient,
                              ScheduledExecutorService executorService, Bot bot) {
        super(bot);
        this.urlService = urlService;
        this.vkClient = vkClient;
        this.executorService = executorService;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);
        if (user.getUrl() == null) {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, POLL_FAIL_REPLY_MESSAGE, "")));
        }

        PollingService pollingService = new PollingService(chatId, urlService, vkClient);
        PostingService postingService = new PostingService(chatId, bot, urlService);
        CheckRunnableService checkService = new CheckRunnableService(chatId, executorService, urlService);

        log.info("Starting polling, posting and checking threads...");
        user.setRegistered(true);
        urlService.saveBotUser(user);
        executorService.scheduleAtFixedRate(pollingService, 0, 3, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(postingService, 5, 3, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(checkService, 5, 5, TimeUnit.SECONDS);
        return Collections.emptyList();
    }
}
