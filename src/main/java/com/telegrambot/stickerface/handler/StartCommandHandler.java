package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.dto.CommandEnum;
import com.telegrambot.stickerface.listener.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StartCommandHandler extends AbstractHandler {

    private static final String START_REPLY_MESSAGE = "Hello, %s! So let's start! Choose action from menu";

    private final ReplyKeyboardMarkup keyboard;

    StartCommandHandler(Bot bot, ReplyKeyboardMarkup keyboard) {
        super(bot);
        this.keyboard = keyboard;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException {
        String userName;
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        if (message.getChat().getUserName() != null) {
            userName = message.getChat().getUserName();
            keyboardRows = createKeyboard();
        } else if (message.getChat().getFirstName() != null) {
            userName = message.getChat().getFirstName() + message.getChat().getLastName();
            keyboardRows = createKeyboard();
        } else {
            userName = message.getChat().getTitle();
        }

        keyboard.setKeyboard(keyboardRows);

        return Collections.singletonList(bot.execute(this.getDefaultMessage(chatId, START_REPLY_MESSAGE, userName, keyboard)));
    }

    private List<KeyboardRow> createKeyboard() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (int i = 0; i < CommandEnum.values().length; i += 3) {
            if (CommandEnum.values()[i].equals(CommandEnum.NOT_A_COMMAND)
                    || CommandEnum.values()[i].equals(CommandEnum.SERVICE)) {
                continue;
            }

            KeyboardRow row = new KeyboardRow();
            row.add(createButton(CommandEnum.values()[i].getValue()));

            if (i != CommandEnum.values().length - 1) {
                row.add(createButton(CommandEnum.values()[i + 1].getValue()));
                if (i != CommandEnum.values().length - 2) {
                    row.add(createButton(CommandEnum.values()[i + 2].getValue()));
                }
            }
            keyboardRows.add(row);
        }
        return keyboardRows;
    }

    private KeyboardButton createButton(String text) {
        return KeyboardButton.builder()
                .text(text)
                .build();
    }
}
