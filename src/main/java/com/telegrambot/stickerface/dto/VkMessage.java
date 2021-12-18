package com.telegrambot.stickerface.dto;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.time.OffsetDateTime;

@Getter
@Setter
public class VkMessage {

    private String text;

    private SendPhoto image;

    private OffsetDateTime postDate;
}
