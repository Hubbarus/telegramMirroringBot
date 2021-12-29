package com.telegrambot.stickerface.listener;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.dto.Command;
import com.telegrambot.stickerface.handler.AbstractHandler;
import com.telegrambot.stickerface.handler.DeleteMessageHandler;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.service.MirroringUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ListenerFactory factory;

    @Autowired
    private MirroringUrlService urlService;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Bot received message!");

        AbstractListener listener = factory.createListener(update);

        Long chatId = listener.getChatId();
        log.info("ChatId: " + chatId);

        if (urlService.getBotUserByChatId(chatId) == null) {
            BotUser user = new BotUser();
            user.setUser(chatId > 0);
            user.setChatId(chatId);
            urlService.saveBotUser(user);
        }

        try {
            String inputText = listener.getInputText();
            Command command = Command.fromString(inputText.split(" ")[0]);

            log.info("Command: " + command);

            AbstractHandler handler = getHandler(command);
            log.info("Handler chosen: " + handler.getClass());

            List<Message> sentMessages = handler.handle(chatId, listener.getMessage());

            Thread thread = new Thread(new DeleteMessageHandler(this, sentMessages));
            thread.start();
        } catch (TelegramApiException e) {
            log.error(String.format("Error while command processing. ChatId: %s. %n Exception: %s %n", chatId, e.getMessage()));
            e.printStackTrace();
        } catch (InterruptedException e) {
            log.error(String.format("Error while multithreading. ChatId: %s. %n Exception: %s %n", chatId, e.getMessage()));
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private AbstractHandler getHandler(Command command) {
        Object bean = context.getBean(command.getValue().toLowerCase().replaceAll("/", "")
                + "CommandHandler");
        return (AbstractHandler) bean;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}