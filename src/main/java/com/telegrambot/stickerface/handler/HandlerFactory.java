package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.dto.CommandEnum;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HandlerFactory {

    private final VkClientConfig vkClientConfig;
    private final MirroringUrlService urlService;
    private final VkApiClient vkApiClient;
    private final Bot bot;

    @Autowired
    public HandlerFactory(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot) {
        this.vkClientConfig = vkClientConfig;
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
        this.bot = bot;
    }

    public AbstractHandler getHandler(CommandEnum command) {
        switch (command) {
            case START:
                return new StartCommandHandler(bot);
            case LOGIN:
                return new LoginCommandHandler(vkClientConfig, urlService, bot);
            case REGISTER:
                return new RegisterCommandHandler(bot, urlService);
            case START_POLL:
                return new PollCommandHandler(urlService, vkApiClient, bot);
            case HELP:
                return new HelpCommandHandler(bot);
            case INFO:
                return new InfoCommandHandler(bot);
            case STATUS:
                return new StatusCommandHandler(bot, urlService);
            case STOP:
                return new StopCommandHandler(urlService, bot);
            default:
                return new DefaultCommandHandler(bot);
        }
    }
}
