package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.HerokuConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.util.MenuUtil;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

public class DeleteSubscriptionHandler extends AbstractHandler implements BotHandler {

    private static final String FAIL_NOT_LOGGED_IN_REPLY_MESSAGE = "Please \"/login\" first!";
    private static final String DELETE_SUBSCRIPTION_READY_REPLY_MESSAGE = "Please choose community to unsubscribe...";
    private static final String FAIL_NO_COMMUNITIES_REPLY_MESSAGE = "You are subscribed to no communities!";

    DeleteSubscriptionHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard, HerokuConfig herokuConfig) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard, herokuConfig);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException, ClientException, ApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        if (user != null) {
            if (user.getVkCommunities() != null) {
                bot.setDeleteCommandCalled(true);
                List<KeyboardRow> keyboardRows = MenuUtil.createCommunitiesKeyboard(user.getVkCommunities());
                keyboard.setKeyboard(keyboardRows);
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, DELETE_SUBSCRIPTION_READY_REPLY_MESSAGE, "", keyboard)));
            } else {
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, FAIL_NO_COMMUNITIES_REPLY_MESSAGE, "", null)));
            }
        } else {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, FAIL_NOT_LOGGED_IN_REPLY_MESSAGE, "", null)));
        }
    }
}
