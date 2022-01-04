package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.telegrambot.stickerface.repository.BotUserRepository;
import com.telegrambot.stickerface.repository.VkCommunityRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Service
@Getter
public class MirroringUrlService {

    private static final String URL_VALIDATION_REGEX = "^(?:http(s)?://)?(vk.com|vkontakte.ru)+/[^/]*$";
    private final BotUserRepository botUserRepository;
    private final VkCommunityRepository communityRepository;
    protected ConcurrentLinkedQueue<VkMessage> messageQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    public MirroringUrlService(BotUserRepository botUserRepository, VkCommunityRepository communityRepository) {
        this.botUserRepository = botUserRepository;
        this.communityRepository = communityRepository;
    }

    public boolean isUrlValid(String urlToValidate) {
        return urlToValidate.matches(URL_VALIDATION_REGEX);
    }

    public BotUser getBotUserByChatId(Long chatId) {
        return botUserRepository.findByChatId(chatId);
    }

    public void saveBotUser(BotUser user) {
        botUserRepository.save(user);
    }

    public void saveCommunity(VkCommunity community) {
        communityRepository.save(community);
    }
}