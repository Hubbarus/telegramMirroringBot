package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.repository.BotUserRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Service
@Getter
public class MirroringUrlService {

    private static final String URL_VALIDATION_REGEX = "((([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+(:[0-9]+)?|(?:www.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w\\-_]*)?\\??(?:[-+=&;%@.\\w_]*)#?(?:[\\w]*))?)";
    private final BotUserRepository repository;
    protected ConcurrentLinkedQueue<VkMessage> messageQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    public MirroringUrlService(BotUserRepository repository) {
        this.repository = repository;
    }

    public boolean isUrlValid(String urlToValidate) {
        return urlToValidate.matches(URL_VALIDATION_REGEX);
    }

    public BotUser getBotUserByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }

    public void saveBotUser(BotUser user) {
        repository.save(user);
    }
}