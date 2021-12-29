package com.telegrambot.stickerface.repository;

import com.telegrambot.stickerface.model.BotUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotUserRepository extends CrudRepository<BotUser, Long> {

    BotUser findByUserId(String userId);

    BotUser findByChatId(Long chatId);
}
