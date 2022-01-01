package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.dto.CommandEnum;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
public class HandlerFactory {

    private final VkClientConfig vkClientConfig;
    private final MirroringUrlService urlService;
    private final VkApiClient vkApiClient;
    private final Bot bot;
    private final BotConfig botConfig;
    private final ReplyKeyboardMarkup keyboard;

    @Autowired
    public HandlerFactory(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        this.vkClientConfig = vkClientConfig;
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
        this.bot = bot;
        this.botConfig = botConfig;
        this.keyboard = keyboard;
    }

    public AbstractHandler getHandler(CommandEnum command) {
        switch (command) {
            case START:
                return new StartCommandHandler(bot, keyboard);
            case LOGIN:
                return new LoginCommandHandler(vkClientConfig, urlService, bot);
            case REGISTER:
                return new RegisterCommandHandler(bot, urlService, vkApiClient);
            case START_POLL:
                return new PollCommandHandler(urlService, vkApiClient, bot, botConfig);
            case HELP:
                return new HelpCommandHandler(bot);
            case INFO:
                return new InfoCommandHandler(bot);
            case STATUS:
                return new StatusCommandHandler(bot, urlService);
            case STOP:
                return new StopCommandHandler(urlService, bot);
            case SERVICE:
                return new ServiceHandler(bot);
            case DELETE:
            default:
                return new DefaultCommandHandler(bot);
        }
    }
}
