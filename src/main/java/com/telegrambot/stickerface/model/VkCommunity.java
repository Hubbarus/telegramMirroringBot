package com.telegrambot.stickerface.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "VK_CMMT")
public class VkCommunity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "communityTid_generator")
    @SequenceGenerator(name = "communityTid_generator", sequenceName = "SEQUENCE_VK_CMMT", allocationSize = 1)
    private Long communityTid;

    @Column(name = "CMMT_NAME")
    private String name;

    @Column(name = "CMMT_URL")
    private String url;

    @Column(name = "GRP_ID")
    private Integer groupId;

    @Column(name = "LST_PST_DT")
    private LocalDateTime lastPostedDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "BOT_USR_TID")
    @JsonIgnore
    private BotUser botUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VkCommunity community = (VkCommunity) o;
        return Objects.equals(name, community.name) && Objects.equals(url, community.url) && Objects.equals(groupId, community.groupId) && Objects.equals(lastPostedDate, community.lastPostedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, groupId, lastPostedDate);
    }

    public LocalDateTime getLastPostedDate() {
        return lastPostedDate;
    }

    public void setLastPostedDate(LocalDateTime lastPostedDate) {
        this.lastPostedDate = lastPostedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public BotUser getBotUser() {
        return botUser;
    }

    public void setBotUser(BotUser botUser) {
        this.botUser = botUser;
    }

}
