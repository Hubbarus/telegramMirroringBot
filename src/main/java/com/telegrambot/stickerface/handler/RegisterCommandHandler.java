package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.handler.exception.UrlNotValidException;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import com.vk.api.sdk.objects.groups.responses.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RegisterCommandHandler extends AbstractHandler {

    private static final String REGISTER_FAIL_REPLY_MESSAGE = "No url specified in input or more than one url provided. Check input!";
    private static final String REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE = "Invalid url: %s";
    private static final String REGISTER_FAIL_ALREADY_SUBSCRIBED_REPLY_MESSAGE = "You are already registered %s";
    private static final String REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE = "User not logged in! Please call /login first";
    private static final String REGISTER_SUCCESS_REPLY_MESSAGE = "Successfully registered community: %s";

    private final MirroringUrlService urlService;
    private final VkApiClient vkApiClient;

    RegisterCommandHandler(Bot bot, MirroringUrlService urlService, VkApiClient vkApiClient) {
        super(bot);
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws TelegramApiException, ClientException, ApiException {
        deleteOwnMessage(chatId, message);
        BotUser user = urlService.getBotUserByChatId(chatId);

        if (user.isLoggedIn()) {
            String[] input = message.getText().split(" ");

            if (input.length != 2) {
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_REPLY_MESSAGE, "")));
            } else {
                log.info("Registering url " + input[1]);
                try {
                    VkCommunity community = validateAndCreateCommunity(input[1], user);
                    user.addVkCommunity(community);
                    urlService.saveBotUser(user);
                } catch (UrlNotValidException e) {
                    log.error(e.getMessage());
                    return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE, input[1])));
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                    return Collections.singletonList(bot.execute(getDefaultMessage(chatId, e.getMessage(), "")));
                }
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_SUCCESS_REPLY_MESSAGE, input[1])));
            }
        } else {
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_NOT_LOGGED_IN_REPLY_MESSAGE, "")));
        }
    }

    private VkCommunity validateAndCreateCommunity(String communityUrl, BotUser user) throws UrlNotValidException, ClientException, ApiException {
        if (!urlService.isUrlValid(communityUrl)) {
            throw new UrlNotValidException("Url not valid: " + communityUrl);
        }

        String path = communityUrl.replaceAll("http.+/", "");
        if (user.getVkCommunities().stream()
                .anyMatch(comm -> comm.getUrl().equalsIgnoreCase(path))) {
            throw new IllegalArgumentException("Already subscribed");
        }

        VkCommunity community = new VkCommunity();
        UserActor actor = getActor(user);
        setGroupIdAndGroupName(actor, path, community);
        community.setUrl(path);

        return community;
    }

    private void setGroupIdAndGroupName(UserActor actor, String url, VkCommunity community) throws ClientException, ApiException {
        GetResponse userGroupsResponse = vkApiClient
                .groups()
                .get(actor)
                .execute();

        List<GetByIdObjectLegacyResponse> groups = vkApiClient.groups()
                .getByIdObjectLegacy(actor)
                .groupIds(userGroupsResponse.getItems()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList()))
                .execute();

        GetByIdObjectLegacyResponse group = groups.stream()
                .filter(gr -> gr.getScreenName().equalsIgnoreCase(url))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Problems getting groupId, may be you are not a group member?"));

        community.setName(group.getName());
        community.setGroupId(group.getId());
    }

    private UserActor getActor(BotUser user) {
        return new UserActor(Integer.parseInt(user.getUserId()), user.getToken());
    }
}