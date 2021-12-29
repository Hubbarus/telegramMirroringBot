package com.telegrambot.stickerface.dto;

import lombok.Getter;

@Getter
public enum CommandEnum {
    START("/start"),
    STATUS("/status"),
    HELP("/help"),
    INFO("/info"),
    REGISTER("/register"),
    START_POLL("/poll"),
    STOP("/stop"),
    LOGIN("/login"),
    NOT_A_COMMAND("default");

    String value;

    CommandEnum(String value) {
        this.value = value;
    }

    public static CommandEnum fromString(String value) {
        for (CommandEnum command : CommandEnum.values()) {
            if (command.value.equals(value)) {
                return command;
            }
        }
        return NOT_A_COMMAND;
    }
}