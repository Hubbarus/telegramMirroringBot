package com.telegrambot.stickerface.dto;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.time.LocalDateTime;

@Getter
@Setter
public class VkMessage {

    private SendMediaGroup mediaGroup;

    private SendPhoto image;

    private SendMessage message;

    private LocalDateTime postDate;

    private String communityName;
}
