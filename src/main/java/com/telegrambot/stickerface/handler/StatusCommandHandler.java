package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.telegrambot.stickerface.service.MirroringUrlService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatusCommandHandler extends AbstractHandler {

    private static final String STATUS_REPLY_MESSAGE = "Bot is now mirroring %s";

    private final MirroringUrlService urlService;

    StatusCommandHandler(Bot bot, MirroringUrlService urlService) {
        super(bot);
        this.urlService = urlService;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        String communityString = user.getVkCommunities().isEmpty() ? "nothing" : user.getVkCommunities().stream()
                .map(VkCommunity::getName)
                .map(name -> "'".concat(name).concat("'"))
                .collect(Collectors.joining(", "));

        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, STATUS_REPLY_MESSAGE, communityString)));
    }
}
