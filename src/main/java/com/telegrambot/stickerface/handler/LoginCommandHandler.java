package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.service.MirroringUrlService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class LoginCommandHandler extends AbstractHandler {

    private static final String LOGIN_REPLY_MESSAGE = "Please login by using link below.";
    private static final String LOGIN_SUCCESSFUL_REPLY_MESSAGE = "Log in successful! %s";
    private static final String LOGIN_FAILED_REPLY_MESSAGE = "Log failed! %s";

    private final VkClientConfig vkClientConfig;

    private final MirroringUrlService urlService;

    @Autowired
    public LoginCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService) {
        this.vkClientConfig = vkClientConfig;
        this.urlService = urlService;
    }

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        synchronized (urlService) {
            if (!urlService.isLoggedIn()) {
                String urlTemplate = UriComponentsBuilder.fromHttpUrl(vkClientConfig.getTokenUrl())
                        .queryParam("client_id", String.valueOf(vkClientConfig.getAppId()))
                        .queryParam("scope", "offline")
                        .queryParam("redirect_uri", vkClientConfig.getRedirectUri())
                        .queryParam("display", "page")
                        .queryParam("response_type", "token")
                        .queryParam("state", "authorizeBot")
                        .encode()
                        .toUriString();

                bot.execute(getDefaultMessage(chatId, message, LOGIN_REPLY_MESSAGE, ""));
                bot.execute(getDefaultMessage(chatId, message, urlTemplate, ""));

                try {
                    urlService.wait(vkClientConfig.getWaitingLoginTime());
                } catch (InterruptedException e) {
                    bot.execute(getDefaultMessage(chatId, message, LOGIN_FAILED_REPLY_MESSAGE, ""));
                }

                if (urlService.isLoggedIn()) {
                    bot.execute(getDefaultMessage(chatId, message, LOGIN_SUCCESSFUL_REPLY_MESSAGE, ""));
                } else {
                    bot.execute(getDefaultMessage(chatId, message, LOGIN_FAILED_REPLY_MESSAGE, "Maximum waiting time exceeded!"));
                }
            } else {
                bot.execute(getDefaultMessage(chatId, message, LOGIN_SUCCESSFUL_REPLY_MESSAGE, "Already logged in!"));
            }
        }
    }
}