package com.telegrambot.stickerface.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "BOT_USR")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "botUserTid_generator")
    @SequenceGenerator(name = "botUserTid_generator", sequenceName = "SEQUENCE_BOT_USR", allocationSize = 1)
    private Long botUserTid;

    @Column(name = "URL")
    private String url;

    @Column(name = "IS_REG")
    private boolean isRegistered;

    @Column(name = "IS_LOGIN")
    private boolean isLoggedIn;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "USR_ID")
    private String userId;

    @Column(name = "CHT_ID")
    @NotNull
    private Long chatId;

    @Column(name = "IS_USR")
    @NotNull
    private boolean isUser;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotUser)) return false;
        BotUser user = (BotUser) o;
        return isRegistered() == user.isRegistered() &&
                isLoggedIn() == user.isLoggedIn() &&
                isUser() == user.isUser() &&
                Objects.equals(getUrl(), user.getUrl()) &&
                Objects.equals(getToken(), user.getToken()) &&
                Objects.equals(getUserId(), user.getUserId()) &&
                Objects.equals(getChatId(), user.getChatId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), isRegistered(), isLoggedIn(), getToken(), getUserId(), getChatId(), isUser());
    }
}
