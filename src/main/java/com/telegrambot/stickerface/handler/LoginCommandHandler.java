package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class LoginCommandHandler extends AbstractHandler {

    private static final String LOGIN_REPLY_MESSAGE = "Please login by using link below.";
    private static final String LOGIN_SUCCESSFUL_REPLY_MESSAGE = "Log in successful! %s";
    private static final String LOGIN_FAILED_REPLY_MESSAGE = "Log failed! %s";

    private final VkClientConfig vkClientConfig;

    private final MirroringUrlService urlService;

    @Autowired
    public LoginCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, Bot bot) {
        super(bot);
        this.vkClientConfig = vkClientConfig;
        this.urlService = urlService;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        List<Message> sentMessages = new ArrayList<>();
        BotUser user = urlService.getBotUserByChatId(chatId);
        synchronized (urlService) {
            if (!user.isLoggedIn()) {
                String urlTemplate = UriComponentsBuilder.fromHttpUrl(vkClientConfig.getTokenUrl())
                        .queryParam("client_id", String.valueOf(vkClientConfig.getAppId()))
                        .queryParam("scope", "offline")
                        .queryParam("redirect_uri", vkClientConfig.getRedirectUri())
                        .queryParam("display", "page")
                        .queryParam("response_type", "token")
                        .queryParam("state", String.valueOf(chatId))
                        .encode()
                        .toUriString();

                sentMessages.add(bot.execute(getDefaultMessage(chatId, LOGIN_REPLY_MESSAGE, "", null)));
                Message urlMessage = bot.execute(getDefaultMessage(chatId, urlTemplate, "", null));

                try {
                    urlService.wait(vkClientConfig.getWaitingLoginTime());
                } catch (InterruptedException e) {
                    sentMessages.add(bot.execute(getDefaultMessage(chatId, LOGIN_FAILED_REPLY_MESSAGE, "", null)));
                    Thread.currentThread().interrupt();
                }

                BotUser updatedUser = urlService.getBotUserByChatId(chatId);

                if (updatedUser.isLoggedIn()) {
                    sentMessages.add(bot.execute(getDefaultMessage(chatId, LOGIN_SUCCESSFUL_REPLY_MESSAGE, "", null)));
                } else {
                    sentMessages.add(bot.execute(getDefaultMessage(chatId, LOGIN_FAILED_REPLY_MESSAGE, "Maximum waiting time exceeded!", null)));
                }
                deleteOwnMessage(chatId, urlMessage);
            } else {
                sentMessages.add(bot.execute(getDefaultMessage(chatId, LOGIN_SUCCESSFUL_REPLY_MESSAGE, "Already logged in!", null)));
            }
        }
        return sentMessages;
    }
}