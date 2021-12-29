package com.telegrambot.stickerface.controller;

import com.telegrambot.stickerface.dto.AuthCallback;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Log4j2
public class CallbackController {

    private final MirroringUrlService urlService;

    @Autowired
    public CallbackController(MirroringUrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/callback")
    public synchronized ResponseEntity<String> callback(@RequestBody AuthCallback body) {
        log.info("Login callback received!");
        String token = body.getToken();
        String userId = body.getUserId();
        String chatId = body.getState();
        if (token != null && !token.isEmpty() && !token.isBlank()
                && userId != null && !userId.isEmpty() && !userId.isBlank()) {
            synchronized (urlService) {
                log.info("Token exists!");
                BotUser user = urlService.getBotUserByChatId(Long.parseLong(chatId));
                if (!user.isLoggedIn()) {
                    user.setToken(token);
                    user.setUserId(userId);
                    user.setLoggedIn(true);
                    urlService.saveBotUser(user);
                } else {
                    log.error("Already logged in!");
                }
                urlService.notifyAll();
            }
        } else {
            log.error("No token or userId provided in callback!");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}