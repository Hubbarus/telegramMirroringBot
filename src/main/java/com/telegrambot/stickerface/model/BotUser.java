package com.telegrambot.stickerface.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "BOT_USR")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "botUserTid_generator")
    @SequenceGenerator(name = "botUserTid_generator", sequenceName = "SEQUENCE_BOT_USR", allocationSize = 1)
    @Column(name = "BOT_USR_TID")
    private Long botUserTid;

    @Column(name = "IS_STOP")
    private boolean isStopped = true;

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

    @OneToMany(mappedBy = "botUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VkCommunity> vkCommunities = new ArrayList<>();

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
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

    public List<VkCommunity> getVkCommunities() {
        return vkCommunities;
    }

    public void setVkCommunities(List<VkCommunity> vkCommunities) {
        if (this.vkCommunities != null) {
            this.vkCommunities.forEach(comm -> comm.setBotUser(null));
        }
        if (vkCommunities != null) {
            vkCommunities.forEach(comm -> comm.setBotUser(this));
        }
        this.vkCommunities = vkCommunities;
    }

    public void addVkCommunity(VkCommunity vkCommunity) {
        if (vkCommunities == null) {
            vkCommunities = new ArrayList<>();
        }
        if (vkCommunity != null) {
            vkCommunities.add(vkCommunity);
            vkCommunity.setBotUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotUser botUser = (BotUser) o;
        return isStopped == botUser.isStopped && isLoggedIn == botUser.isLoggedIn && isUser == botUser.isUser && Objects.equals(token, botUser.token) && Objects.equals(userId, botUser.userId) && Objects.equals(chatId, botUser.chatId) && Objects.equals(vkCommunities, botUser.vkCommunities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isStopped, isLoggedIn, token, userId, chatId, isUser, vkCommunities);
    }
}
