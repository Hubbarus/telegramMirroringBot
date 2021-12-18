package com.telegrambot.stickerface.listener;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.dto.Command;
import com.telegrambot.stickerface.handler.AbstractHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private ApplicationContext context;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Bot received message!");

        Long chatId = update.getMessage().getChatId();
        log.info("ChatId: " + chatId);

        try {
            String inputText = update.getMessage().getText();
            Command command = Command.fromString(inputText.split(" ")[0]);

            log.info("Command: " + command);

            AbstractHandler handler = getHandler(command);
            log.info("Handler chosen: " + handler.getClass());

            handler.handle(chatId, update.getMessage());
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.setChatId(String.valueOf(chatId));
//            sendMessage.setText(text);
//            execute(sendMessage);
//
//            if (update.getMessage().getPhoto() != null) {
//                for (PhotoSize photo : update.getMessage().getPhoto()) {
//
//                    String fileId = photo.getFileId();
//
//                    GetFile getFile = new GetFile();
//                    getFile.setFileId(fileId);
//                    String filePath = execute(getFile).getFilePath();
//                    File file = downloadFile(filePath, new File("./data/userDoc/" + fileId + ".jpg"));
//                    System.out.println();
//                }
//            }
        } catch (TelegramApiException e) {
            log.error(String.format("Error while command processing. ChatId: %s. \n\n Exception: %s \n\n", chatId, e.getMessage()));
            e.printStackTrace();
        } catch (InterruptedException e) {
            log.error(String.format("Error while multithreading. ChatId: %s. \n\n Exception: %s \n\n", chatId, e.getMessage()));
            e.printStackTrace();
        }
    }

    private AbstractHandler getHandler(Command command) {
        Object bean = context.getBean(command.getValue().toLowerCase().replaceAll("/", "")
                + "CommandHandler");
        return (AbstractHandler) bean;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
