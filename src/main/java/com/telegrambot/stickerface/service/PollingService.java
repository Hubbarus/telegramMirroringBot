package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.config.BotConfig;
import com.telegrambot.stickerface.dto.VkMessage;
import com.telegrambot.stickerface.model.BotUser;
import com.telegrambot.stickerface.model.VkCommunity;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostAttachmentType;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class PollingService implements Runnable {

    private static final String UP_ARROW_EMOJI = new String(Character.toChars(0x2B06));
    public static final String VK_DIRECT_GROUP_URL = "https://vk.com/wall%s_%s";

    private final MirroringUrlService urlService;
    private final VkApiClient vkApiClient;
    private final UserActor actor;
    private final Integer groupId;
    private final String groupName;
    private final int communitiesCount;
    private final BotUser user;
    private final BotConfig botConfig;

    public PollingService(MirroringUrlService urlService, VkApiClient vkApiClient, UserActor actor,
                          VkCommunity community, int communitiesCount, BotUser user, BotConfig botConfig) {
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
        this.actor = actor;
        this.groupId = community.getGroupId();
        this.groupName = community.getName();
        this.communitiesCount = communitiesCount;
        this.user = user;
        this.botConfig = botConfig;
    }

    @Override
    public void run() {
        log.info("Requesting host...");
        Optional<VkCommunity> communityOptional = user.getVkCommunities().stream()
                .filter(comm -> comm.getGroupId().equals(groupId))
                .findFirst();

        AtomicReference<LocalDateTime> initialDelay = getInitialPostingDelay(communityOptional);

        try {
            List<WallpostFull> dateFilteredPosts = getAndFilterWallPosts(initialDelay);

            AtomicInteger newMessagesCount = new AtomicInteger();
            dateFilteredPosts.stream().filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Wallpost::getDate))
                    .forEach(post -> {
                        VkMessage vkMessage = createVkMessage(post);
                        urlService.getMessageQueue().add(vkMessage);
                        newMessagesCount.getAndIncrement();

                        log.info("Last polled post was in: " + initialDelay.get() + ". Now will be updated!");
                        initialDelay.set(convertDate(post.getDate()));
                    });

            if (communityOptional.isPresent()) {
                VkCommunity community = communityOptional.get();
                if (community.getLastPostedDate() == null || community.getLastPostedDate().isBefore(initialDelay.get())) {
                    community.setLastPostedDate(initialDelay.get());
                    urlService.saveCommunity(community);
                }
            }

            log.info("Polling succesfully finished!");
            int count = newMessagesCount.get();
            if (count != 0) {
                log.info("New messages polled from '" + groupName + "': " + count);
            } else {
                log.info("No new messages from '" + groupName + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<WallpostFull> getAndFilterWallPosts(AtomicReference<LocalDateTime> initialDelay) throws ClientException, ApiException {
        GetResponse response = vkApiClient
                .wall()
                .get(actor)
                .ownerId(groupId)
                .count(50)
                .execute();

        return response.getItems().stream()
                .filter(post -> {
                    LocalDateTime dateTime = Instant.ofEpochSecond(post.getDate())
                            .atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
                    return dateTime.isAfter(initialDelay.get());
                })
                .collect(Collectors.toList());
    }

    private AtomicReference<LocalDateTime> getInitialPostingDelay(Optional<VkCommunity> communityOptional) {
        return communityOptional.map(VkCommunity::getLastPostedDate)
                .map(lastDate -> lastDate.plus(1, ChronoUnit.SECONDS))
                .map(AtomicReference::new)
                .orElse(new AtomicReference<>(LocalDateTime.now().minusHours(botConfig.getPostsDelayHours())));
    }

    private VkMessage createVkMessage(WallpostFull post) {
        log.info("Creating message from '" + groupName + "' community...");
        VkMessage vkMessage = new VkMessage();
        vkMessage.setCommunityName(groupName);
        vkMessage.setPostDate(convertDate(post.getDate()));
        String postText = formatPostText(post.getText());

        List<WallpostAttachment> attachments = Optional.ofNullable(post.getAttachments())
                .orElseGet(() -> post.getCopyHistory()
                        .stream()
                        .map(Wallpost::getAttachments)
                        .findFirst()
                        .orElse(null));
        if (attachments != null && !attachments.isEmpty()) {
            if (attachments.size() > 1 && attachments.stream()
                    .filter(att -> att.getType().equals(WallpostAttachmentType.PHOTO))
                    .count() != 1) {
                log.info("WallPost has multiple attachments.");
                createMediaGroup(attachments, postText, vkMessage, post);
            } else if (attachments.size() == 1) {
                log.info("WallPost has only one attachment.");
                createSingleMedia(attachments.get(0), postText, vkMessage, post);
            } else {
                attachments.forEach(att -> createSingleMedia(att, postText, vkMessage, post));
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(postText);
            vkMessage.setNotPhotoMessage(sendMessage);
        }
        return vkMessage;
    }

    private void createSingleMedia(WallpostAttachment attachment, String postText, VkMessage vkMessage, WallpostFull post) {
        if (attachment.getType().equals(WallpostAttachmentType.PHOTO)) {
            SendPhoto image = new SendPhoto();

            Optional<InputFile> photoAttachment = createInputFile(attachment);
            photoAttachment.ifPresent(image::setPhoto);

            if (postText != null && !postText.isEmpty()) {
                if (postText.length() >= 200) {
                    createSeparateMessage(postText, vkMessage);
                } else {
                    image.setCaption(postText);
                }
            }

            vkMessage.setImage(image);
        } else {
            setDirectLinkToWallPost(vkMessage, post.getId());
        }
    }

    private void createMediaGroup(List<WallpostAttachment> attachments, String postText, VkMessage vkMessage, WallpostFull post) {
        SendMediaGroup group = new SendMediaGroup();
        List<InputMedia> medias = new ArrayList<>();

        for (WallpostAttachment attachment : attachments) {
            if (attachment.getType().equals(WallpostAttachmentType.PHOTO)) {
                createInputMedia(attachment).ifPresent(medias::add);
            } else {
                setDirectLinkToWallPost(vkMessage, post.getId());
            }
        }

        if (postText != null && !postText.isEmpty() && !medias.isEmpty()) {
            if (postText.length() >= 200) {
                createSeparateMessage(postText, vkMessage);
            } else {
                InputMedia media = medias.get(0);
                media.setCaption(postText);
            }
        }

        if (!medias.isEmpty()) {
            group.setMedias(medias);
            vkMessage.setMediaGroup(group);
        }
    }

    private Optional<InputMedia> createInputMedia(WallpostAttachment att) {
        Photo photo = att.getPhoto();
        return photo.getSizes().stream()
                .max(Comparator.comparing(PhotoSizes::getHeight))
                .map(PhotoSizes::getUrl)
                .map(this::readAndConvertImage)
                .filter(Objects::nonNull)
                .map(is -> {
                    InputMedia media = new InputMediaPhoto();
                    try {
                        media.setMedia(is, "att" + photo.getAccessKey() + ".jpeg");
                        is.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return media;
                });
    }

    private Optional<InputFile> createInputFile(WallpostAttachment att) {
        Photo photo = att.getPhoto();
        return photo.getSizes().stream()
                .max(Comparator.comparing(PhotoSizes::getHeight))
                .map(PhotoSizes::getUrl)
                .map(this::readAndConvertImage)
                .filter(Objects::nonNull)
                .map(is -> {
                    InputFile media = new InputFile();
                    try {
                        media.setMedia(is, "att" + photo.getAccessKey() + ".jpeg");
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return media;
                });
    }

    private InputStream readAndConvertImage(URI imageUrl) {
        try {
            URL url = imageUrl.toURL();
            byte[] buf = IOUtils.toByteArray(url);
            return new ByteArrayInputStream(buf);
        } catch (IOException ex) {
            log.error("Error reading file!");
            ex.printStackTrace();
            return null;
        }
    }

    private LocalDateTime convertDate(Integer date) {
        return Instant.ofEpochSecond(date).atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
    }

    private String formatPostText(String postText) {
        return communitiesCount > 1 ? "\t\t".concat(groupName).concat("\n\n").concat(postText) : postText;
    }

    private void createSeparateMessage(String postText, VkMessage vkMessage) {
        log.info("Too long post, will be set to separate message.");
        SendMessage message = new SendMessage();
        message.setText(UP_ARROW_EMOJI.concat(postText));
        vkMessage.setMessageCaption(message);
    }

    private void setDirectLinkToWallPost(VkMessage vkMessage, Integer postId) {
        SendMessage message = new SendMessage();
        String postUrl = String.format(VK_DIRECT_GROUP_URL, groupId, postId);
        message.setText(postUrl);
        vkMessage.setNotPhotoMessage(message);
    }
}