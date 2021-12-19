package com.telegrambot.stickerface.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import com.vk.api.sdk.objects.groups.responses.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PollingService implements Runnable {

    @Autowired
    private MirroringUrlService urlService;

    @Autowired
    private VkApiClient vkApiClient;

    @Override
    public void run() {
        log.info("Requesting host...\n");
        try {
            UserActor actor = new UserActor(Integer.parseInt(urlService.getUserId()), urlService.getToken());
            GetResponse execute1 = vkApiClient.groups().get(actor).execute();
            List<GetByIdObjectLegacyResponse> execute = vkApiClient.groups().getByIdObjectLegacy(actor)
                    .groupIds(execute1.getItems()
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.toList()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}