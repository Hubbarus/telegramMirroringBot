package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.CheckRunnableService;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.service.PollingService;
import com.telegrambot.stickerface.service.PostingService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import com.vk.api.sdk.objects.groups.responses.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PollCommandHandler extends AbstractHandler {

    private static final String POLL_FAIL_REPLY_MESSAGE = "No url specified. Run /register command first!";

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
        if (user.getUrl() == null) {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, POLL_FAIL_REPLY_MESSAGE, "")));
        }

        UserActor actor = getActor(user);
        Integer groupId = getGroupId(actor, user);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

        PollingService pollingService = new PollingService(chatId, urlService, vkClient, actor, Math.negateExact(groupId));
        PostingService postingService = new PostingService(chatId, bot, urlService);
        CheckRunnableService checkService = new CheckRunnableService(chatId, executorService, urlService);

        log.info("Starting polling, posting and checking threads...");
        user.setRegistered(true);
        urlService.saveBotUser(user);

        executorService.scheduleAtFixedRate(pollingService, botConfig.getInitialPollingDelay(), botConfig.getPollingPeriod(), TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(postingService, botConfig.getInitialPostingDelay(), botConfig.getPostingPeriod(), TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(checkService, 5, 5, TimeUnit.SECONDS);

        return Collections.emptyList();
    }

    private UserActor getActor(BotUser user) {
        return new UserActor(Integer.parseInt(user.getUserId()), user.getToken());
    }

    private Integer getGroupId(UserActor actor, BotUser user) throws ClientException, ApiException {
        GetResponse userGroupsResponse = vkClient
                .groups()
                .get(actor)
                .execute();

        List<GetByIdObjectLegacyResponse> groups = vkClient.groups()
                .getByIdObjectLegacy(actor)
                .groupIds(userGroupsResponse.getItems()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList()))
                .execute();

        return groups.stream()
                .filter(group -> user.getUrl().contains(group.getScreenName()))
                .findFirst()
                .map(Group::getId)
                .orElseThrow(() -> new IllegalArgumentException("Problems getting groupId"));
    }
}
