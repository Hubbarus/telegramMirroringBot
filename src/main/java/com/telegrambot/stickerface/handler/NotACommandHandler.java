package com.telegrambot.stickerface.handler;

import com.telegrambot.stickerface.handler.exception.UrlNotValidException;
import com.telegrambot.stickerface.listener.Bot;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.telegrambot.stickerface.service.MirroringUrlService;
import com.telegrambot.stickerface.util.MenuUtil;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import com.vk.api.sdk.objects.groups.responses.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NotACommandHandler extends AbstractHandler {

    private static final String REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE = "Invalid URL: %s\n" +
            "Try again, or type \"/skip\" to abort URL registration!";
    private static final String REGISTER_FAIL_ALREADY_SUBSCRIBED_REPLY_MESSAGE = "You are already registered %s";
    private static final String REGISTER_SUCCESS_REPLY_MESSAGE = "Successfully registered community: %s";
    private static final String CHOOSE_ACTION_REPLY_MESSAGE = "Choose action from menu...";
    private static final String DEFAULT_REPLY_MESSAGE = "Please choose actions from menu.\n" +
            "Or type \"/help\" for see all actions.";

    private final MirroringUrlService urlService;
    private final VkApiClient vkApiClient;
    private final ReplyKeyboardMarkup keyboard;

    NotACommandHandler(Bot bot, MirroringUrlService urlService, VkApiClient vkApiClient, ReplyKeyboardMarkup keyboard) {
        super(bot);
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
        this.keyboard = keyboard;
    }

    @Override
    public List<Message> handle(long chatId, Message message) throws Exception {
        BotUser user = urlService.getBotUserByChatId(chatId);
        String input = message.getText();

        if (input.equalsIgnoreCase("/skip")) {
            List<KeyboardRow> keyboardRows = MenuUtil.createKeyboard();
            keyboard.setKeyboard(keyboardRows);
            bot.setRegisterCommandCalled(false);
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, CHOOSE_ACTION_REPLY_MESSAGE, "", keyboard)));
        }

        if (bot.isRegisterCommandCalled()) {
            log.info("Registering url " + input);
            try {
                VkCommunity community = validateAndCreateCommunity(input, user);
                user.addVkCommunity(community);
                urlService.saveBotUser(user);
                bot.setRegisterCommandCalled(false);
            } catch (UrlNotValidException e) {
                log.error(e.getMessage());
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_FAIL_URL_NOT_VALID_REPLY_MESSAGE, input, null)));
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
                return Collections.singletonList(bot.execute(getDefaultMessage(chatId, e.getMessage(), "", null)));
            }
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, REGISTER_SUCCESS_REPLY_MESSAGE, input, null)));
        } else {
            deleteOwnMessage(chatId, message);
            return Collections.singletonList(bot.execute(getDefaultMessage(chatId, DEFAULT_REPLY_MESSAGE, "", null)));

        }
    }

    private VkCommunity validateAndCreateCommunity(String communityUrl, BotUser user) throws UrlNotValidException, ClientException, ApiException {
        if (!urlService.isUrlValid(communityUrl)) {
            throw new UrlNotValidException("Url not valid: " + communityUrl);
        }

        String path = communityUrl.replaceAll("http.+/", "");
        if (user.getVkCommunities().stream()
                .anyMatch(comm -> comm.getUrl().equalsIgnoreCase(path))) {
            throw new IllegalArgumentException(String.format(REGISTER_FAIL_ALREADY_SUBSCRIBED_REPLY_MESSAGE, path));
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
