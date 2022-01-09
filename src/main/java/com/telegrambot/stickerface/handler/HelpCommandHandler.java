package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

public class HelpCommandHandler extends AbstractHandler implements BotHandler {

    private static final String HELP_REPLY_MESSAGE =
            "Type \"/login\" to login to your VK account. This you have to do to register group to poll posts. \n" +
                    "Type \"/register <<community_url>>\" to register VK comminity to poll posts. \n" +
                    "After registration type \"/poll\" to start polling posts.\n\n" +
                    "Type \"/status\" to see what VK community is mirrored. \n" +
                    "Type \"/info\" to see information about developer and contacts. \n" +
                    "Type \"/stop\" if you want to stop this bot. \n";

    HelpCommandHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient, Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        super(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, HELP_REPLY_MESSAGE, "", null)));
    }
}