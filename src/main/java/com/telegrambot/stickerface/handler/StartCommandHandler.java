package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.HerokuConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.util.MenuUtil;
import com.vk.api.sdk.client.VkApiClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartCommandHandler extends AbstractHandler implements BotHandler {

    private static final String START_REPLY_MESSAGE = "Hello, %s! So let's start! Choose action from menu";

    StartCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard, HerokuConfig herokuConfig) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard, herokuConfig);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        String userName;
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        if (message.getChat().getUserName() != null) {
            userName = message.getChat().getUserName();
            keyboardRows = MenuUtil.createMenuKeyboard();
        } else if (message.getChat().getFirstName() != null) {
            userName = message.getChat().getFirstName() + message.getChat().getLastName();
            keyboardRows = MenuUtil.createMenuKeyboard();
        } else {
            userName = message.getChat().getTitle();
        }

        keyboard.setKeyboard(keyboardRows);
        return Collections.singletonList(bot.execute(this.getDefaultMessage(chatId, START_REPLY_MESSAGE, userName, keyboard)));
    }
}
