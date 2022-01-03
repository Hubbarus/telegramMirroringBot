package com.telegrambot.stickerface.repository;

import com.telegrambot.stickerface.model.VkCommunity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VkCommunityRepository extends CrudRepository<VkCommunity, Long> {
}
