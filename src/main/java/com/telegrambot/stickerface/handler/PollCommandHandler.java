package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.telegrambot.stickerface.service.CheckRunnableService;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.service.PollingService;
import com.telegrambot.stickerface.service.PostingService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PollCommandHandler extends AbstractHandler implements BotHandler {

    private static final String POLL_FAIL_REPLY_MESSAGE = "No url specified. Run /register command first!";

    private ScheduledThreadPoolExecutor executor;

    PollCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient,
                       Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException, ClientException, ApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        List<VkCommunity> vkCommunities = user.getVkCommunities();
        if (vkCommunities.isEmpty()) {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, POLL_FAIL_REPLY_MESSAGE, "", null)));
        }

        UserActor actor = getActor(user);

        log.info("Creating threads to post and poll...");
        List<PollingService> pollingCommunityList = getPollingThreads(user, actor, vkCommunities.size());

        log.info("Starting polling, posting and checking threads...");
        user.setStopped(false);
        urlService.saveBotUser(user);

        startScheduledTaskExecutor(pollingCommunityList, chatId);
        return Collections.emptyList();
    }

    private UserActor getActor(BotUser user) {
        return new UserActor(Integer.parseInt(user.getUserId()), user.getToken());
    }

    private List<PollingService> getPollingThreads(BotUser user, UserActor actor, int communitiesCount) {
        List<PollingService> pollingCommunityList = new ArrayList<>();
        for (VkCommunity vkCommunity : user.getVkCommunities()) {
            if (!vkCommunity.isPollStarted()) {
                pollingCommunityList.add(new PollingService(urlService, vkApiClient, actor,
                        vkCommunity, communitiesCount, user, botConfig));
                vkCommunity.setPollStarted(true);
                urlService.saveCommunity(vkCommunity);
            }
        }
        return pollingCommunityList;
    }

    private void startScheduledTaskExecutor(List<PollingService> pollingCommunityList, long chatId) {
        int corePoolSize = pollingCommunityList.size() + 2;
        if (executor == null || !executor.isShutdown() || !executor.isTerminated() || !executor.isTerminating()) {
            log.info("Creating thread executor...");
            executor = new ScheduledThreadPoolExecutor(corePoolSize);

            CheckRunnableService checkService = new CheckRunnableService(chatId, executor, urlService);
            PostingService postingService = new PostingService(chatId, bot, urlService);

            executor.schedule(checkService, 5, TimeUnit.SECONDS);
            executor.scheduleAtFixedRate(postingService, botConfig.getInitialPostingDelay(), botConfig.getPostingPeriod(), TimeUnit.SECONDS);
        } else {
            log.info(String.format("Updating thread executor's pool size from %s to %s", executor.getCorePoolSize(),
                    executor.getCorePoolSize() + corePoolSize));
            executor.setCorePoolSize(executor.getCorePoolSize() + corePoolSize);
        }

        for (PollingService pollingService : pollingCommunityList) {
            executor.scheduleAtFixedRate(pollingService, botConfig.getInitialPollingDelay(), botConfig.getPollingPeriod(), TimeUnit.SECONDS);
        }

    }
}
