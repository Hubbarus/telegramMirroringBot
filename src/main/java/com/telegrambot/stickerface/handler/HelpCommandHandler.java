package com.telegrambot.stickerface.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpCommandHandler extends AbstractHandler {

    private static final String HELP_REPLY_MESSAGE = "Type \"/start\" to start bot. \n" +
            "Type \"/register <<community_url>>\" to register VK comminity to poll photos. \n" +
            "After registration type \"/poll\" to start polling posts.\n" +
            "Type \"/status\" to see what VK community is mirrored. \n" +
            "Type \"/info\" to see information about developer and contacts. \n" +
            "Type \"/stop\" if you want to stop this bot. \n";

    @Override
    public void handle(long chatId, Message message) throws TelegramApiException {
        bot.execute(getDefaultMessage(chatId, message, HELP_REPLY_MESSAGE, ""));
    }
}
