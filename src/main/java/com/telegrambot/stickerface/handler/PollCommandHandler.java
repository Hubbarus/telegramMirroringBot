package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PollCommandHandler extends AbstractHandler {

    private static final String POLL_FAIL_REPLY_MESSAGE = "No url specified. Run /register command first!";
    private static final String ALREADY_POLLING_REPLY_MESSAGE = "Already polling! If you want to stop, call /stop command.";

    private final MirroringUrlService urlService;
    private final VkApiClient vkClient;
    private final BotConfig botConfig;

    @Autowired
    public PollCommandHandler(MirroringUrlService urlService, VkApiClient vkClient, Bot bot, BotConfig botConfig) {
        super(bot);
        this.urlService = urlService;
        this.vkClient = vkClient;
        this.botConfig = botConfig;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException, ClientException, ApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        if (user.isStopped()) {
            List<VkCommunity> vkCommunities = user.getVkCommunities();
            if (vkCommunities.isEmpty()) {
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, POLL_FAIL_REPLY_MESSAGE, "")));
            }

            UserActor actor = getActor(user);

            log.info("Creating threads to post and poll...");
            List<PollingService> pollingCommunityList = getPollingThreads(vkCommunities, actor, vkCommunities.size());

            log.info("Starting polling, posting and checking threads...");
            user.setStopped(false);
            urlService.saveBotUser(user);

            startScheduledTaskExecutor(pollingCommunityList, chatId);
        } else {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, ALREADY_POLLING_REPLY_MESSAGE, "")));
        }
        return Collections.emptyList();
    }

    private UserActor getActor(BotUser user) {
        return new UserActor(Integer.parseInt(user.getUserId()), user.getToken());
    }

    private List<PollingService> getPollingThreads(List<VkCommunity> vkCommunities, UserActor actor, int communitiesCount) throws ClientException, ApiException {
        List<PollingService> pollingCommunityList = new ArrayList<>();
        for (VkCommunity vkCommunity : vkCommunities) {
            pollingCommunityList.add(new PollingService(urlService, vkClient, actor, Math.negateExact(vkCommunity.getGroupId()), vkCommunity.getName(), communitiesCount));
        }
        return pollingCommunityList;
    }

    private void startScheduledTaskExecutor(List<PollingService> pollingCommunityList, long chatId) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(pollingCommunityList.size() + 2);

        for (PollingService pollingService : pollingCommunityList) {
            executorService.scheduleAtFixedRate(pollingService, botConfig.getInitialPollingDelay(), botConfig.getPollingPeriod(), TimeUnit.SECONDS);
        }

        CheckRunnableService checkService = new CheckRunnableService(chatId, executorService, urlService);
        PostingService postingService = new PostingService(chatId, bot, urlService);

        executorService.scheduleAtFixedRate(postingService, botConfig.getInitialPostingDelay(), botConfig.getPostingPeriod(), TimeUnit.SECONDS);
        executorService.schedule(checkService, 5, TimeUnit.SECONDS);
    }
}
