package com.telegrambot.stickerface.util;

import com.telegrambot.stickerface.dto.CommandEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MenuUtil {

    private MenuUtil() {
    }

    public static List<KeyboardRow> createKeyboard() {
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

    private static KeyboardButton createButton(String text) {
        return KeyboardButton.builder()
                .text(text)
                .build();
    }
}
