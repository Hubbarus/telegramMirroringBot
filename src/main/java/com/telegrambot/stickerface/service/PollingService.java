package com.telegrambot.stickerface.service;

import com.telegrambot.stickerface.dto.VkMessage;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostAttachmentType;
import com.vk.api.sdk.objects.wall.WallpostFull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class PollingService implements Runnable {

    private static final String UP_ARROW_EMOJI = new String(Character.toChars(0x2B06));
    private final MirroringUrlService urlService;
    private final VkApiClient vkApiClient;
    private final UserActor actor;
    private final Integer groupId;
    private final String groupName;
    private final int communitiesCount;
    private ZonedDateTime lastDateTime = ZonedDateTime.now().minusHours(48);

    public PollingService(MirroringUrlService urlService, VkApiClient vkApiClient, UserActor actor, Integer groupId, String groupName, int communitiesCount) {
        this.urlService = urlService;
        this.vkApiClient = vkApiClient;
        this.actor = actor;
        this.groupId = groupId;
        this.groupName = groupName;
        this.communitiesCount = communitiesCount;
    }

    @Override
    public void run() {
        log.info("Requesting host...");
        try {
            com.vk.api.sdk.objects.wall.responses.GetResponse wallPosts = vkApiClient
                    .wall()
                    .get(actor)
                    .ownerId(groupId)
                    .count(50)
                    .execute();

            List<WallpostFull> dateFilteredPosts = wallPosts.getItems().stream()
                    .filter(post -> {
                        ZonedDateTime dateTime = Instant.ofEpochSecond(post.getDate()).atZone(ZoneId.of("Europe/Moscow"));
                        return dateTime.isAfter(lastDateTime);
                    })
                    .collect(Collectors.toList());

            AtomicInteger newMessagesCount = new AtomicInteger();
            dateFilteredPosts.stream().filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Wallpost::getDate))
                    .forEach(post -> {
                        VkMessage vkMessage = createVkMessage(post);
                        urlService.getMessageQueue().add(vkMessage);
                        newMessagesCount.getAndIncrement();

                        log.info("Last polled post was in: " + lastDateTime.toLocalDateTime() + ". Now will be updated!");
                        lastDateTime = convertDate(post.getDate());
                    });

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

    private VkMessage createVkMessage(WallpostFull post) {
        log.info("Creating message from '" + groupName + "' community...");
        VkMessage vkMessage = new VkMessage();
        vkMessage.setCommunityName(groupName);
        vkMessage.setPostDate(convertDate(post.getDate()).toLocalDateTime());
        String postText = formatPostText(post.getText());

        List<WallpostAttachment> attachments = post.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            if (attachments.size() > 1) {
                log.info("WallPost has multiple attachments.");
                createMediaGroup(attachments, postText, vkMessage);
            } else {
                log.info("WallPost has only one attachment.");
                createSingleMedia(attachments.get(0), postText, vkMessage);
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(postText);
            vkMessage.setMessage(sendMessage);
        }
        return vkMessage;
    }

    private void createSingleMedia(WallpostAttachment attachment, String postText, VkMessage vkMessage) {
        SendPhoto image = new SendPhoto();

        if (attachment.getType().equals(WallpostAttachmentType.PHOTO)) {
            Optional<InputFile> photoAttachment = createSinglePhotoAttachment(attachment);
            photoAttachment.ifPresent(image::setPhoto);

            if (postText != null && !postText.isEmpty()) {
                if (postText.length() >= 200) {
                    log.info("Too long post, will be set to separate message.");
                    SendMessage message = new SendMessage();
                    message.setText(UP_ARROW_EMOJI.concat(postText));
                    vkMessage.setMessage(message);
                } else {
                    image.setCaption(postText);
                }
            }

            vkMessage.setImage(image);
        }
        //TODO add video support
    }

    private void createMediaGroup(List<WallpostAttachment> attachments, String postText, VkMessage vkMessage) {
        SendMediaGroup group = new SendMediaGroup();
        List<InputMedia> medias = new ArrayList<>();

        for (WallpostAttachment att : attachments) {
            if (att.getType().equals(WallpostAttachmentType.PHOTO)) {
                Optional<InputMedia> inputMedia = createGroupPhotoAttachment(att);
                inputMedia.ifPresent(medias::add);
            }
            //TODO add video support
        }

        if (postText != null && !postText.isEmpty() && !medias.isEmpty()) {
            if (postText.length() >= 200) {
                log.info("Too long post, will be set to separate message.");
                SendMessage message = new SendMessage();
                message.setText(UP_ARROW_EMOJI.concat(postText));
                vkMessage.setMessage(message);
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

    private Optional<InputMedia> createGroupPhotoAttachment(WallpostAttachment att) {
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

    private Optional<InputFile> createSinglePhotoAttachment(WallpostAttachment att) {
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
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            BufferedImage read = ImageIO.read(imageUrl.toURL());
            ImageIO.write(read, "jpeg", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException ex) {
            log.error("Error reading file!");
            ex.printStackTrace();
            return null;
        }
    }

    private ZonedDateTime convertDate(Integer date) {
        return Instant.ofEpochSecond(date).atZone(ZoneId.of("Europe/Moscow"));
    }

    private String formatPostText(String postText) {
        return communitiesCount > 1 ? "\t\t".concat(groupName).concat("\n\n").concat(postText) : postText;
    }
}