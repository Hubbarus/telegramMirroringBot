package com.telegrambot.stickerface.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthCallback {

    @JsonProperty("access_token")
    private String token;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("state")
    private String state;
}