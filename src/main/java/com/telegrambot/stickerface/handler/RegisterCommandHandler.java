package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.service.MirroringUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RegisterCommandHandler extends AbstractHandler {

    private static final String REGISTER_FAIL_REPLY_MESSAGE = "No url specified in input or more than one url provided. Check input!";
    private static final String REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE = "Invalid url: %s";
    private static final String REGISTER_SUCCESS_REPLY_MESSAGE = "Successfully registered community: %s";

    @Autowired
    private MirroringUrlService urlService;

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        String[] input = message.getText().split(" ");

        if (input.length != 2) {
            bot.execute(getDefaultMessage(chatId, message, REGISTER_FAIL_REPLY_MESSAGE, ""));
        } else {
            if (urlService.isUrlValid(input[1]) != null) {
                urlService.setUrl(input[1]);
                bot.execute(getDefaultMessage(chatId, message, REGISTER_SUCCESS_REPLY_MESSAGE, urlService.getUrl()));
            } else {
                bot.execute(getDefaultMessage(chatId, message, REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE, input[1]));
            }
        }
    }
}
