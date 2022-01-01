package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.config.VkClientConfig;
import com.telegrambot.stickerface.dto.CommandEnum;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class AbstractHandler implements BotHandler {

    private static final String DEFAULT_REPLY_MESSAGE = "Please choose actions from menu.\n" +
            "Or type \"/help\" for see all actions.";

    protected final VkClientConfig vkClientConfig;
    protected final MirroringUrlService urlService;
    protected final VkApiClient vkApiClient;
    protected final Bot bot;
    protected final BotConfig botConfig;
    protected final ReplyKeyboardMarkup keyboard;

    public AbstractHandler(VkClientConfig vkClientConfig, MirroringUrlService urlService, VkApiClient vkApiClient,
                           Bot bot, BotConfig botConfig, ReplyKeyboardMarkup keyboard) {
        this.vkClientConfig = vkClientConfig;
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
        this.bot = bot;
        this.botConfig = botConfig;
        this.keyboard = keyboard;
    }

    public List<Message> handle(long chatId, Message message) throws Exception {
        deleteOwnMessage(chatId, message);
        return Collections.singletonList(bot.execute(getDefaultMessage(chatId, DEFAULT_REPLY_MESSAGE, "", null)));
    }

    void deleteOwnMessage(long chatId, Message receivedMessage) throws TelegramApiException {
        if (receivedMessage != null) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(chatId));
            deleteMessage.setMessageId(receivedMessage.getMessageId());
            bot.execute(deleteMessage);
        }
    }

    public BotHandler getHandler(CommandEnum command) {
        switch (command) {
            case START:
                return new StartCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case LOGIN:
                return new LoginCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case REGISTER:
                return new RegisterCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case START_POLL:
                return new PollCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case HELP:
                return new HelpCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case INFO:
                return new InfoCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case STATUS:
                return new StatusCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case STOP:
                return new StopCommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case SERVICE:
                return new ServiceHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case NOT_A_COMMAND:
                return new NotACommandHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            case DELETE:
                return new DeleteSubscriptionHandler(vkClientConfig, urlService, vkApiClient, bot, botConfig, keyboard);
            default:
                return this;
        }
    }

}
