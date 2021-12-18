package com.telegrambot.stickerface.dto;

import lombok.Getter;
import org.telegram.bot.kernel.database.DatabaseManager;

@Getter
public enum Command {
    START("/start"),
    STATUS("/status"),
    HELP("/help"),
    INFO("/info"),
    REGISTER("/register"),
    START_POLL("/poll"),
    STOP("/stop"),
    NOT_A_COMMAND("default");

    String value;

    Command(String value) {
        this.value = value;
    }

    public static Command fromString(String value) {
        for (Command command : Command.values()) {
            if (command.value.equals(value)) {
                return command;
            }
        }
        return NOT_A_COMMAND;
    }
}
